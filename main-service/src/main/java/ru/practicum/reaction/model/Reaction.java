package ru.practicum.reaction.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "reactions")
@NoArgsConstructor
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;

    private Integer eventId;

    private ReactionType reactionType;

    public Reaction(Integer userId, Integer eventId, ReactionType reactionType) {
        this.userId = userId;
        this.eventId = eventId;
        this.reactionType = reactionType;
    }
}
