package org.labs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.locks.Condition;

@Getter
@AllArgsConstructor
public class DeveloperState {

    private int developerId;

    @Setter
    private PortionState portionState;

    private Condition portionReadyCondition;

}
