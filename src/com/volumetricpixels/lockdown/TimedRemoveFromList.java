package com.volumetricpixels.lockdown;

class TimedRemoveFromList implements Runnable {
	   
    private Lockdown plugin;
    private Object toRemove;
    private long time;
    private long passedTime;
   
    public TimedRemoveFromList(Lockdown plugin, String toRemove, long timeSeconds) {
        this.time = timeSeconds;
        this.toRemove = toRemove;
    }
 
    @Override
    public void run() {
        while (!(passedTime >= time)) {
            try {
                passedTime++;
                Thread.sleep(1000);
            } catch (Throwable ignoreBecauseSkills) {}
        }
       plugin.remove(toRemove);
    }
 
}
 