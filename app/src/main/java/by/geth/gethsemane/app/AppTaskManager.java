package by.geth.gethsemane.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppTaskManager {
    private static AppTaskManager sInstance;

    public static synchronized AppTaskManager getInstance() {
        if (sInstance == null)
            sInstance = new AppTaskManager();
        return sInstance;
    }

    private List<Task> mTaskList;

    private AppTaskManager() {
        mTaskList = new ArrayList<>();
    }

    public synchronized void add(Task task) {
        mTaskList.add(task);
    }

    public synchronized void remove(Task task) {
        mTaskList.remove(task);
    }

    public synchronized boolean contains(Task task) {
        return mTaskList.contains(task);
    }

    public synchronized boolean isEmpty() {
        return mTaskList.isEmpty();
    }

    public static class Task {
        private String mName;
        private Map<String, Object> mArguments;

        public Task(String name) {
            setName(name);
            mArguments = new HashMap<>();
        }

        public String getName() {
            return mName;
        }

        public void setName(String name) {
            mName = name;
        }

        public Map<String, Object> getArguments() {
            return mArguments;
        }

        @Override
        public int hashCode() {
            return getName().hashCode() + mArguments.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Task) {
                Task otherTask = (Task) o;
                return this.getName().equals(otherTask.getName()) &&
                        this.getArguments().equals(otherTask.getArguments());
            } else {
                return false;
            }
        }
    }
}
