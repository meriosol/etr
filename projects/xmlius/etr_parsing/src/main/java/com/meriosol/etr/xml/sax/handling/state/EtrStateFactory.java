package com.meriosol.etr.xml.sax.handling.state;

/**
 * Creates ETR states.
 */
class EtrStateFactory {
    EtrBaseState buildStartingState(EtrInfoKeeper etrInfoKeeper) {
        return new EventsOpenedState(etrInfoKeeper);
    }

}
