package com.mitrais.polsdemo.model;

import com.mitrais.polsdemo.model.audit.DateAudit;

import javax.persistence.*;

@Entity
@Table(name = "votes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"pollid", "userid"})
})
public class Vote extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pollid", nullable = false)
    private Poll poll;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "choiceid", nullable = false)
    private Choice choice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userid", nullable = false)
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    public Choice getChoice() {
        return choice;
    }

    public void setChoice(Choice choice) {
        this.choice = choice;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
