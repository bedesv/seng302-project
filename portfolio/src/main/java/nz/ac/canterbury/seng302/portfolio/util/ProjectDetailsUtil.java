package nz.ac.canterbury.seng302.portfolio.util;

import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import org.springframework.data.util.Pair;

import java.util.*;

public class ProjectDetailsUtil {

    /**
     * Takes a list of Event objects and a list of Sprint objects and determines which Events occur within a Sprint. If
     * an Event occurs within a Sprint the Event's id number is stored within the Sprint.
     * @param eventList List of Event objects to embed in a Sprint
     * @param sprintList List of Sprint objects which will have Events embedded within
     */
    public static void embedEvents(List<Event> eventList, List<Sprint> sprintList) {
        int completed = 0;
        for (int i = 0; i < eventList.size(); i++) {
            for (Sprint sprint : sprintList) {
                if ((eventList.get(i).getEventStartDate().after(sprint.getStartDate()) || eventList.get(i).getEventStartDate().equals(sprint.getStartDate())) && (eventList.get(i).getEventEndDate().before(sprint.getEndDate()) || eventList.get(i).getEventEndDate().equals(sprint.getEndDate()))) {
                    sprint.addEventsInside(i);
                    eventList.get(i).setColourStart(sprint.getColour());
                    eventList.get(i).setColourEnd(sprint.getColour());
                    completed = completed + 2;
                } else if ((eventList.get(i).getEventStartDate().after(sprint.getStartDate()) || eventList.get(i).getEventStartDate().equals(sprint.getStartDate())) && (eventList.get(i).getEventStartDate().before(sprint.getEndDate()) || eventList.get(i).getEventStartDate().equals(sprint.getEndDate()))) {
                    sprint.addEventsInside(i);
                    eventList.get(i).setColourStart(sprint.getColour());
                    completed++;
                } else if ((eventList.get(i).getEventEndDate().after(sprint.getStartDate()) || eventList.get(i).getEventEndDate().equals(sprint.getStartDate())) && (eventList.get(i).getEventEndDate().before(sprint.getEndDate()) || eventList.get(i).getEventEndDate().equals(sprint.getEndDate()))) {
                    sprint.addEventsInside(i);
                    eventList.get(i).setColourEnd(sprint.getColour());
                    completed++;
                } else if (eventList.get(i).getEventStartDate().before(sprint.getStartDate()) && eventList.get(i).getEventEndDate().after(sprint.getEndDate())) {
                    sprint.addEventsInside(i);
                    completed++;
                }
            }
            if (completed >= 1) {
                eventList.get(i).setCompleted(true);
            }
            completed = 0;
        }
    }

    /**
     * Takes a list of Deadline objects and a list of Sprint objects and determines which Deadlines occur within a Sprint. If
     * a Deadline occurs within a Sprint the Dealines's id number is stored within the Sprint.
     * @param deadlineList List of Deadline objects to embed in a Sprint
     * @param sprintList List of Sprint objects which will have Events embedded within
     */
    public static void embedDeadlines(List<Deadline> deadlineList, List<Sprint> sprintList) {
        for (int i = 0; i < deadlineList.size(); i++) {
            for (Sprint sprint : sprintList) {
                if ((deadlineList.get(i).getDeadlineDate().after(sprint.getStartDate()) || deadlineList.get(i).getDeadlineDate().equals(sprint.getStartDate())) && (deadlineList.get(i).getDeadlineDate().before(sprint.getEndDate()) || deadlineList.get(i).getDeadlineDate().equals(sprint.getEndDate()))) {
                    sprint.addDeadlinesInside(i);
                    deadlineList.get(i).setColour(sprint.getColour());
                    deadlineList.get(i).setCompleted(true);
                }
            }
        }
    }

    /**
     * Takes a list of Event objects and a list of Sprint objects and orders them by Start Date. Returns a list of Pairs
     * which hold the id of the Event or Sprint and a string which describes whether the id is for a Sprint or Event.
     * @param eventList list of Event objects to order
     * @param sprintList list of Sprint objects to order
     * @return list of Pair<Integer, String> objects which hold the id of the object and what type it is
     */
    public static List<Pair<Integer, String>> getOrderedImportantDates(List<Event> eventList, List<Sprint> sprintList, List<Deadline> deadlineList) {
        List<Pair<Integer, String>> importantDates = new ArrayList<>();

//        eventList.sort(Comparator.comparing(Event::getEventStartDate));
//        deadlineList.sort(Comparator.comparing(Deadline::getDeadlineDate));
//
//        int eventIndex = 0;
//        int sprintIndex = 0;
//        int deadlineIndex = 0;
//        while (eventIndex + sprintIndex + deadlineIndex < eventList.size() + sprintList.size() + deadlineList.size()) {
//            if (sprintList)
//        }

        for (int i = 0; i < eventList.size(); i++) {
            if (!eventList.get(i).isCompleted()) {
                importantDates.add(Pair.of(i, "Event"));
            }
        }
        for (int i = 0; i < deadlineList.size(); i++) {
            if (!deadlineList.get(i).isCompleted()) {
                importantDates.add(Pair.of(i, "Deadline"));
            }
        }
        for (int i = 0; i < sprintList.size(); i++) {
            importantDates.add(Pair.of(i, "Sprint"));
        }
        importantDates.sort(Comparator.comparing((Pair<Integer, String> a) -> (a.getSecond().equals("Sprint") ? sprintList.get(a.getFirst()).getStartDate() : a.getSecond().equals("Event") ? eventList.get(a.getFirst()).getEventStartDate() : deadlineList.get(a.getFirst()).getDeadlineDate())));
        return importantDates;
    }

    /**
     * Assigns a color to each Sprint object within sprintList
     * @param sprintList list of Sprint objects to assign colours
     */
    public static void colorSprints(List<Sprint> sprintList) {
        ColourPicker.setColourZero();
        for (Sprint sprint: sprintList) {
            sprint.setColour(ColourPicker.getNextColour());
        }
    }
}
