package edu.eci.arsw.highlandersim;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;

    private int health;

    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());

    private boolean estado;

    private ArrayList<Immortal> aliveImmortals;


    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb, ArrayList<Immortal> aliveImmortals) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue=defaultDamageValue;
        this.aliveImmortals = aliveImmortals;
    }

    public void run() {

        while (true) {

            Immortal im;

            synchronized (aliveImmortals) {

                int nextFighterIndex = r.nextInt(aliveImmortals.size());

                if (aliveImmortals.contains(this)) {

                    int myIndex = aliveImmortals.indexOf(this);

                    //avoid self-fight
                    if (nextFighterIndex == myIndex) {
                        nextFighterIndex = ((nextFighterIndex + 1) % aliveImmortals.size());
                    }

                }


                im = aliveImmortals.get(nextFighterIndex);


                this.fight(im);

                if (im.health == 0) {
                    aliveImmortals.remove(aliveImmortals.indexOf(im));
                }

                if (!aliveImmortals.contains(this)) {
                    aliveImmortals.add(this);
                }

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                pause();

            }
        }

    }

    public void fight(Immortal i2) {

        if (i2.getHealth() > 0) {
            i2.changeHealth(i2.getHealth() - defaultDamageValue);
            this.health += defaultDamageValue;
            updateCallback.processReport("Fight: " + this + " vs " + i2+"\n");
        } else {
            updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
        }

    }

    public void changeHealth(int v) {
        health = v;
    }

    public int getHealth() {
        return health;
    }

    @Override
    public String toString() {

        return name + "[" + health + "]";
    }

    public synchronized void pause(){
        while(estado){
            try{
                wait();

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public synchronized void hold(){
        estado = true;
    }

    public synchronized void restart(){
        estado = false;
        notifyAll();
    }

}
