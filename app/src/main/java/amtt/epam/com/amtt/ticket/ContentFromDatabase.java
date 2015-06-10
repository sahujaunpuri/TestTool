package amtt.epam.com.amtt.ticket;

import java.util.List;

import amtt.epam.com.amtt.bo.issue.createmeta.JIssueTypes;
import amtt.epam.com.amtt.bo.issue.createmeta.JProjects;
import amtt.epam.com.amtt.bo.project.JPriority;
import amtt.epam.com.amtt.database.object.DbObjectManger;
import amtt.epam.com.amtt.database.object.IResult;
import amtt.epam.com.amtt.database.table.IssuetypeTable;
import amtt.epam.com.amtt.database.table.PriorityTable;
import amtt.epam.com.amtt.database.table.ProjectTable;

/**
 * @author Iryna Monchanka
 * @version on 6/10/2015
 */

public class ContentFromDatabase {

    public static void getProjects(int idUser, IResult<List<JProjects>> result) {
        DbObjectManger.INSTANCE.query(new JProjects(), null, new String[]{ProjectTable._ID_USER}, new String[]{String.valueOf(idUser)}, result);
    }

    public static void getIssueTypes(String projectKey, IResult<List<JIssueTypes>> result) {
        DbObjectManger.INSTANCE.query(new JIssueTypes(), null, new String[]{IssuetypeTable._KEY_PROJECT}, new String[]{projectKey}, result);
    }

    public static void getPriorities(String url, IResult<List<JPriority>> result) {
        DbObjectManger.INSTANCE.query(new JPriority(), null, new String[]{PriorityTable._URL}, new String[]{url}, result);
    }

    public static void getLastProject(String lastProjectKey, IResult<List<JProjects>> result) {
        DbObjectManger.INSTANCE.query(new JProjects(), null, new String[]{ProjectTable._KEY}, new String[]{lastProjectKey}, result);
    }
}
