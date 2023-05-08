package ru.practicum.compilation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "compilations_events")
@AllArgsConstructor
@NoArgsConstructor
public class CompilationEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "compilation_id")
    private Integer compilationId;

    @Column(name = "event_id")
    private Integer eventId;

    public CompilationEvent(Integer compilationId, Integer eventId) {
        this.compilationId = compilationId;
        this.eventId = eventId;
    }
}
