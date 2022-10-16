package com.group18.activityclassification;

import java.util.LinkedList;

public class Queue {

    private final java.util.Queue<Attribute> queue;
    private final int queueLength = 125;
    private int counter;

    public Queue() {
        this.queue = new LinkedList<>();
        this.counter = 0;

        for(int i = 0; i < queueLength; i++){
            queue.add(null);
        }
    }

    public void addToQueue(Attribute activity){
        queue.remove();
        queue.add(activity);
        this.counter++;
    }

    public boolean isReady() {
        return this.counter >= this.queueLength;
    }

    public Attribute tallyQueue(){
        int[] tallyArray = {0, 0, 0, 0, 0, 0, 0};
        java.util.Queue<Attribute> tallyQueue = new LinkedList<>(queue);
        this.counter = 0;

        for(int i = 0; i < queueLength; i++){
            Attribute type = tallyQueue.poll();
            if (type == null) continue;

            switch(type) {
                case WALKING:
                    tallyArray[0]++;
                    break;
                case STANDING:
                    tallyArray[1]++;
                    break;
                case JOGGING:
                    tallyArray[2]++;
                    break;
                case SITTING:
                    tallyArray[3]++;
                    break;
                case BIKING:
                    tallyArray[4]++;
                    break;
                case UPSTAIRS:
                    tallyArray[5]++;
                    break;
                case DOWNSTAIRS:
                    tallyArray[6]++;
                    break;
                default:
                    break;
            }
        }

        int indexMax = 0;
        for(int i = 0; i < 7; i++){
            indexMax = tallyArray[i] > tallyArray[indexMax] ? i : indexMax;
        }

        //WALKING POSTPROCESSING:
        if(tallyArray[5]<(queueLength/2)&&
                tallyArray[6]<(queueLength/2)&&
                (tallyArray[0]+tallyArray[5]+tallyArray[6])>(7*queueLength/10)){
            return Attribute.WALKING;
        }

        switch(indexMax){
            case 0:
                return Attribute.WALKING;
            case 1:
                return Attribute.STANDING;
            case 2:
                return Attribute.JOGGING;
            case 3:
                return Attribute.SITTING;
            case 4:
                return Attribute.BIKING;
            case 5:
                return Attribute.UPSTAIRS;
            case 6:
                return Attribute.DOWNSTAIRS;
            default:
                return null;
        }
    }
}
