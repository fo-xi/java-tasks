package ru.mail.polis.homework.oop.vet.Animals;

import ru.mail.polis.homework.oop.vet.*;

public class Pigeon extends Animal implements WildAnimal {

    private String organizationName;

    public Pigeon() {
        super(2);
    }

    @Override
    public String say() {
        return "curls-curls";
    }

    @Override
    public MoveType moveType() {
        return MoveType.FLY;
    }

    @Override
    public String getOrganizationName() {
        return organizationName;
    }

    @Override
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
}
