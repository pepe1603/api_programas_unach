package com.unach.api_pp_sc_rp.service.impl;

import com.unach.api_pp_sc_rp.dto.EventoDTO;
import com.unach.api_pp_sc_rp.events.EventoActualizadoEvent;
import com.unach.api_pp_sc_rp.events.EventoCreadoEvent;
import com.unach.api_pp_sc_rp.events.EventoEliminadoEvent;
import com.unach.api_pp_sc_rp.exception.EntityNotFoundException;
import com.unach.api_pp_sc_rp.mapper.EventoMapper;
import com.unach.api_pp_sc_rp.mapper.TipoProgramaMapper;
import com.unach.api_pp_sc_rp.model.Evento;
import com.unach.api_pp_sc_rp.model.TipoPrograma;
import com.unach.api_pp_sc_rp.model.enums.EstadoEvento;
import com.unach.api_pp_sc_rp.repository.EventoRepository;
import com.unach.api_pp_sc_rp.service.EventoService;
import com.unach.api_pp_sc_rp.service.TipoProgramaService;
import com.unach.api_pp_sc_rp.service.export.ExportService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *  #5 -->> Application publiser  es par apoer ala escuha los eventos donde se realizan el CRUD
 *  Función: Este servicio se encarga de publicar (disparar) los eventos que representan acciones
 *  importantes (como la creación de un nuevo evento) en tu aplicación.
 *
 *  En este caso se coloca despues de guardar,eliminar, actualizar
 * */
@Service
@AllArgsConstructor
public class EventoServiceImpl  implements EventoService {


    private final EventoRepository eventoRepository;
    private final EventoMapper eventoMapper;
    private final TipoProgramaService programaService;
    private final ApplicationEventPublisher eventPublisher;
    private final ExportService exportService;
    private  static final Logger logger = LoggerFactory.getLogger(EventoServiceImpl.class);

    @Override
    public EventoDTO saveEvento(EventoDTO evento) {

        //verificar campos de entrada
        if (evento == null) {
            throw new IllegalArgumentException("No se ha proporcionado datos para el nuevo evento");
        }

        if (evento.getTitulo() == null || evento.getTitulo().isEmpty()) {
            throw new IllegalArgumentException("El titulo del evento no puede estar vacía");
        } else if (evento.getDescripcion() == null || evento.getDescripcion().isEmpty()) {
            throw new IllegalArgumentException("La descripcion del evento no puede estar vacía");
        } else if (evento.getFechaEvento() == null) {
            throw new IllegalArgumentException("La fecha para el evento no puede estar vacía");
        }else if (evento.getIdTipoPrograma() == null ) {
            throw new IllegalArgumentException("El ID del programa para evento no puede estar vacía");
        }

        ///actrualizar
        TipoPrograma programaFounded = TipoProgramaMapper.INSTANCE.toEntity(
                programaService.findByIdTipoPrograma(evento.getIdTipoPrograma())
                        .orElseThrow( () -> new EntityNotFoundException("Programa no encontrado con ID: "+ evento.getIdTipoPrograma())
                        )
        );

        Evento newEvento = new Evento();
        newEvento.setTitulo(evento.getTitulo());
        newEvento.setDescripcion(evento.getDescripcion());
        newEvento.setFechaEvento(evento.getFechaEvento());
        newEvento.setTipoPrograma(programaFounded); // establecemos al relacion
        newEvento.setEstadoEvento(EstadoEvento.PENDIENTE);//establecemos por defecto Pedniente


        EventoDTO savedEvento = eventoMapper.toDTO(
                eventoRepository.save(newEvento));
        //pubicamos evento de  creacion
        eventPublisher.publishEvent(new EventoCreadoEvent(this, evento.getTitulo()));
        return savedEvento;
    }

    @Override
    public Optional<EventoDTO> findByIdEvento(Long id) {
        return eventoRepository.findById(id)
                .map(eventoMapper::toDTO)
                .or(() -> {
                    throw new EntityNotFoundException("Evento no encontrado con ID: "+id);
                });
    }

    @Override
    public List<EventoDTO> findAllEventos() {
        List<Evento> eventos = eventoRepository.findAll();
            if (eventos.isEmpty()){
                throw new EntityNotFoundException("No se encontraron eventos en el repositorio");
            }
        return eventos
                .stream()
                .map(eventoMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public List<EventoDTO> getEventosEntreFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Ambas fechas deben ser proporcionadas");
        }

        List<Evento> eventos = eventoRepository.findByFechaEventoBetween(fechaInicio, fechaFin);
        if (eventos.isEmpty()) {
            throw new EntityNotFoundException("No se encontraron eventos en el rango de fechas proporcionado");
        }

        return eventos.stream()
                .map(eventoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventoDTO updateEvento (Long id, Map <String, Object> updates){

        Evento eventoFounded = eventoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Evento no encontrado enel repositorio"));
                updates.forEach((key, value) -> {
                    if (value == null){
                        throw new IllegalArgumentException("El valor  para [ "+key+" ] no puede ser nulo");
                    }

                    updateField(eventoFounded, key, value);
                });

                EventoDTO updatedEvento=eventoMapper.toDTO(eventoRepository.save(eventoFounded));
        //publicar esatdo de eventoactualizado
        eventPublisher.publishEvent( new EventoActualizadoEvent(this, updatedEvento.getTitulo()));

        return updatedEvento;
    }
    @Transactional
    @Override
    public void updateEstadoEvento(Long id, String nuevoEstado) {

        if (nuevoEstado == null || nuevoEstado.isEmpty()) {
            throw new IllegalArgumentException("El nuevo estado para el evento no puede estar vacío.");
        }

        // Convertir el nuevo estado a Enum
        EstadoEvento estadoEvento;
        try {
            estadoEvento = EstadoEvento.valueOf(nuevoEstado.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("El valor para [Estado Evento] debe ser uno de los valores válidos: [PENDIENTE, EN_CURSO, FINALIZADO, CANCELADO].");
        }

        // Buscar el evento
        Evento eventoFounded = eventoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Evento no encontrado con ID: " + id));

        // Lógica para cambiar el estado
        switch (estadoEvento) {
            case PENDIENTE:
                if (eventoFounded.getEstadoEvento() == EstadoEvento.CANCELADO) {
                    eventoFounded.setEstadoEvento(estadoEvento);
                } else {
                    throw new IllegalStateException("No se puede poner en PENDIENTE desde el estado actual: " + eventoFounded.getEstadoEvento());
                }
                break;

            case EN_CURSO:
                if (eventoFounded.getEstadoEvento() == EstadoEvento.PENDIENTE) {
                    eventoFounded.setEstadoEvento(estadoEvento);
                } else {
                    throw new IllegalStateException("No se puede iniciar el evento desde el estado actual: " + eventoFounded.getEstadoEvento());
                }
                break;

            case FINALIZADO:
                if (eventoFounded.getEstadoEvento() == EstadoEvento.EN_CURSO) {
                    eventoFounded.setEstadoEvento(estadoEvento);
                } else {
                    throw new IllegalStateException("Solo se puede finalizar un evento que está EN CURSO.");
                }
                break;

            case CANCELADO:
                eventoFounded.setEstadoEvento(estadoEvento); // Se puede cancelar en cualquier estado
                break;

            default:
                throw new IllegalArgumentException("Estado desconocido: " + estadoEvento);
        }

        // Guardar el evento actualizado
        eventPublisher.publishEvent(new EventoActualizadoEvent(this, "El evento [ "+eventoFounded.getTitulo()+" ] ha sido Actualizado al estado "+estadoEvento));

        eventoRepository.save(eventoFounded);

    }


    @Override
    public void deleteByIdEvento(Long id) {

        Optional<EventoDTO> evento = this.findByIdEvento(id);
        if (evento.isPresent()){
            eventoRepository.deleteById(id);

            //publicar evento eliminado
            eventPublisher.publishEvent(new EventoEliminadoEvent(this, evento.get().getTitulo()));
        }else {
            throw  new RuntimeException("No se pudo elimnar el evento por que no se encontro");
        }
    }

    @Override
    public Resource exportEventosToCSV() {
        List<EventoDTO> eventos = findAllEventos();
        if (eventos.isEmpty()){
            throw new EntityNotFoundException("No hay datos para exportar");
        }

        List<String> headers = List.of("ID", "Titulo", "Descripcion", "Estado", "Fecha Evento", "Id-ProgramaAsociado");
        List<List<String>> data = eventos.stream()
                .map(actividad -> List.of(
                        actividad.getId().toString(),
                        actividad.getTitulo().toString(),
                        actividad.getDescripcion(),
                        actividad.getEstadoEvento(),
                        actividad.getFechaEvento().toString(),
                        // Verificar si IdTipo Programa es null, devolver "No asignado"
                        actividad.getIdTipoPrograma() != null ? actividad.getIdTipoPrograma().toString() : "No asignado"

                ))
                .collect(Collectors.toList());
        if (data.isEmpty()) {
            logger.warn("No hay datos para exportar al CSV.");
        }

        return exportService.exportToCSV(headers, data);
    }

    @Override
    public Resource exportEventosToPDF() {
        List<EventoDTO> eventos = findAllEventos();
        if (eventos.isEmpty()){
            throw new EntityNotFoundException("No hay datos para exportar");
        }

        List<String> headers = List.of("ID", "Titulo", "Descripcion", "Estado", "Fecha Evento", "Id-ProgramaAsociado");
        List<List<String>> data = eventos.stream()
                .map(actividad -> List.of(
                        actividad.getId().toString(),
                        actividad.getTitulo().toString(),
                        actividad.getDescripcion(),
                        actividad.getEstadoEvento(),
                        actividad.getFechaEvento().toString(),
                        actividad.getIdTipoPrograma().toString()

                ))
                .collect(Collectors.toList());
        if (data.isEmpty()) {
            logger.warn("No hay datos para exportar al CSV.");
        }

        String title = "Reporte de Eventos";
        return exportService.exportToPDF(title, headers, data);
    }

    private void updateField(Evento evento, String key, Object value){
        switch (key) {
            case "titulo" :
                evento.setTitulo((String) value);
                break;

            case "descripcion" :
                evento.setDescripcion((String) value);
                break;
            case "fechaEvento" :
                evento.setFechaEvento((LocalDateTime) value);
                break;
            case "tipoPrograma":

                if (  value instanceof Integer){
                    Long idTprograma = ((Integer) value).longValue();
                    TipoPrograma tipoProgramaFounded = TipoProgramaMapper.INSTANCE.toEntity(
                            programaService.findByIdTipoPrograma( idTprograma)
                                    .orElseThrow(() -> new IllegalArgumentException("Tipo program no encvontradop con ID: "+idTprograma))
                    );

                    evento.setTipoPrograma(tipoProgramaFounded);
                }else if (value instanceof Long){
                    TipoPrograma tipoProgramaFounded = TipoProgramaMapper.INSTANCE.toEntity(
                            programaService.findByIdTipoPrograma(  (Long) value)
                                    .orElseThrow(() -> new IllegalArgumentException("Tipo program no encvontradop con ID: "+ (Long) value))
                    );

                    evento.setTipoPrograma(tipoProgramaFounded);

                }else {
                    throw new IllegalArgumentException("El valor para idTipo Programa debe tener un numero entero");
                }
                break;
            default: throw new RuntimeException("Campo desconocido para la actualizacion: "+ key);
        }
    }
}
