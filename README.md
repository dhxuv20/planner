# planner
This is a very simple console app using Java that serves as a planner for tracking school work. All school work is modeled as a task.
A task has the following attributes:
1. Name
2. Subject
3. Due date
4. Creation date
5. Completion date
6. Status (can be Not Started, In Progress, Done)
7. Size (small, medium, large)

Use cases supported currently:
1. Add a new task
    1. Take attributes of the task
    2. Add to existing list
    3. Persist to a file
2. List all tasks
    1. Hydrate from file when the app starts
    2. Expose the following views:
        1. All tasks
        2. All in-progress tasks sorted by due date
        3. All completed tasks sorted by due date
        4. All tasks created after a certain date
3. Remove a task
    1. Display all tasks created after a certain date and prompt for index number to delete
    2. Delete and persist to file



