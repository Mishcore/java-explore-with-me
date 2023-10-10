package ru.practicum.ewm.utility;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.event.model.Event;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.practicum.ewm.Constants.TIME_FORMATTER;

@UtilityClass
public class EventViewsManager {

    public static Map<Integer, Long> getEventViewsMap(StatsClient statsClient, List<Event> eventList) {
        Map<Integer, Long> eventViews = new HashMap<>();
        String[] uris = new String[eventList.size()];
        for (int i = 0; i < eventList.size(); i++) {
            uris[i] = "/events/" + eventList.get(i).getId();
        }
        List<ViewStats> viewStatsList =
                statsClient.getStats(
                                LocalDateTime.of(1999, 1, 1, 0, 0, 0).format(TIME_FORMATTER),
                                LocalDateTime.of(9999, 1, 1, 0, 0, 0).format(TIME_FORMATTER),
                                uris, true)
                        .getBody();
        for (ViewStats viewStats : viewStatsList) {
            String uri = viewStats.getUri();
            Integer eventId = Integer.parseInt(uri.split("/events/")[1]);
            eventViews.put(eventId, viewStats.getHits());
        }
        return eventViews;
    }

    public static Long getEventViews(StatsClient statsClient, Event event) {
        Map<Integer, Long> eventViews = getEventViewsMap(statsClient, List.of(event));
        if (eventViews.isEmpty()) {
            return 0L;
        } else {
            return eventViews.get(event.getId());
        }
    }

    public static void saveEndpointHit(StatsClient statsClient, HttpServletRequest request) {
        statsClient.saveHit(new EndpointHitDto(
                "ewm-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now().format(TIME_FORMATTER)));
    }
}
