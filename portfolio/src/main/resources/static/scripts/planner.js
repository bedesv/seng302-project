// Define variables that need to be used across functions
let calendarEl;
let calendar;

/**
 * Adds an event listener to the page loading to create the calendar and set it to the project dates
 */
document.addEventListener('DOMContentLoaded', function() {

    const fullMonthStartDate = calculateFullMonthStartDate(projectStartDate);
    const fullMonthEndDate = calculateFullMonthEndDate(dayAfterProjectEndDate);


    calendarEl = document.getElementById('calendar');
    calendar = new FullCalendar.Calendar(calendarEl, {
        validRange: {
            start: fullMonthStartDate,
            end: fullMonthEndDate
        },
        events: [
            // Event to grey out dates not in project
            {
                start: projectStartDate,
                end: dayAfterProjectEndDate,
                display: 'inverse-background',
                backgroundColor: "#CCCCCC"
            },
            // Events for project start and end dates
            {
                title: projectName + " Starts",
                start: projectStartDate,
                end: projectStartDate,
                display: 'background',
                backgroundColor: "#F4EAE6"
            }, {
                title: projectName + " Ends",
                start: projectEndDate,
                end: projectEndDate,
                display: 'background',
                backgroundColor: "#F4EAE6"
            }

        ],
        initialView: 'dayGridMonth',
        initialDate: projectStartDate,
        contentHeight: "auto"
    });
    addSprintsToCalendar();
    calendar.render();
});

/**
 * Calculates the date of the beginning of the given month
 * @param startDate The start date of a project
 * @returns {string} A date string of the beginning of the given month
 */
function calculateFullMonthStartDate(startDate) {
    return startDate.slice(0,8) + "01"
}

/**
 * Calculates the date of the beginning of the month after the given month
 * Date is not inclusive in fullcalendar library, hence why we need the day
 * after the end of the month.
 * endDate is already the day after the project ends, hence why we don't need
 * to calculate anything if endDate is already the first of the month
 * @param endDate The end date of a project
 * @returns {string} A date string of the beginning of the month after the given month
 */
function calculateFullMonthEndDate(endDate) {
    let month;

    // Check if project end date is already the first of the next month (end date is already day after the project end date)
    if (endDate.slice(8) !== "01") {
        // Increase the month by 1
        month = parseInt(endDate.slice(5, 7), 10) + 1;
    } else {
        return endDate;
    }

    // Ensure the month has two digits
    if (month <= 9) {
        month = "0" + month.toString();
    } else {
        month.toString();
    }

    // Calculate and return the end date
    return endDate.slice(0,5) + month + "-" + "01";
}

/**
 * Adds all the sprints in the list created by thymeleaf to the calendar
 */
function addSprintsToCalendar() {
    for (let sprint of sprints) {
        console.log(sprint)
        calendar.addEvent(sprint);
    }
}
