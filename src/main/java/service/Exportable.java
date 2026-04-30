package service;

import java.util.List;

import model.Team;

public interface Exportable {

    void export(List<Team> teams, int projectId) throws Exception;

}