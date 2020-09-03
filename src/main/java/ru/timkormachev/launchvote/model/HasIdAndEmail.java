package ru.timkormachev.launchvote.model;

public interface HasIdAndEmail extends HasId {
    String getEmail();
}