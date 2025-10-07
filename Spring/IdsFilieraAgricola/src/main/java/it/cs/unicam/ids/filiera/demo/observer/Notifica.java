package it.cs.unicam.ids.filiera.demo.observer;

public interface Notifica {

    void sub(Observer o);
    void unsub(Observer o);
    void notifyObservers(String message);

}
