package machine.automatic.decryption.task.creator;

import machine.Machine;
import object.automatic.decryption.data.task.details.DecryptionTaskDetails;
import object.automatic.decryption.difficulty.DecryptionDifficulty;
import object.automatic.decryption.data.input.DecryptionInputData;
import object.machine.configuration.MachineConfiguration;
import object.numbering.RomanNumber;
import org.paukov.combinatorics3.Generator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DecryptionTasksCreator extends Thread {
    private final MachineConfiguration initialMachineConfiguration;
    private final BlockingQueue<DecryptionTaskDetails> tasksQueue;
    //private final BlockingQueue<DecryptionTaskResults> resultsQueue;
    private final DecryptionDifficulty decryptionDifficulty;
    private final int tasksSize;
    private final List<List<Integer>> allRotorIDOptions;
    private final List<List<Character>> allRotorStartPositionsOptions;
    private final List<RomanNumber> allReflectorIDOptions;
    private final Machine machine;
    private final Runnable onTaskCreated;

    public DecryptionTasksCreator(DecryptionInputData decryptionInputData, Machine machine, BlockingQueue<DecryptionTaskDetails> tasksQueue, Runnable onTaskCreated) {
        setName("Decryption Task Creator");
        setDaemon(true);
        this.initialMachineConfiguration = machine.getInitialMachineConfiguration();
        this.machine = machine;
        this.tasksQueue = tasksQueue;
        //this.resultsQueue = resultsQueue;
        this.decryptionDifficulty = decryptionInputData.getDecryptionDifficulty();
        this.tasksSize = decryptionInputData.getTasksSize();
        this.onTaskCreated = onTaskCreated;
        allRotorIDOptions = getAllRotorIDOptions();
        allRotorStartPositionsOptions = getAllRotorStartPositionsOptions();
        allReflectorIDOptions = getAllReflectorIDOptions();
    }

    public int getAmountOfTotalTasks() {
        return allRotorIDOptions.size() * allRotorStartPositionsOptions.size() * allReflectorIDOptions.size() / tasksSize;
    }

    private List<List<Integer>> getAllRotorIDOptions() {
        List<List<Integer>> allRotorIDOptions = new ArrayList<>();

        if (decryptionDifficulty.getIntValue() <= 2) {
            allRotorIDOptions.add(initialMachineConfiguration.getRotorIDsInOrder());
        } else if (decryptionDifficulty.getIntValue() == 3) {
            allRotorIDOptions.addAll(Generator.permutation(initialMachineConfiguration.getRotorIDsInOrder())
                    .simple()
                    .stream()
                    .collect(Collectors.toList()));
        } else  {
            Generator.combination(IntStream.range(1, machine.getMachineStorage().getAllRotors().size() + 1).boxed().collect(Collectors.toList()))
                            .simple(machine.getActiveRotorsCount())
                                    .stream()
                                            .forEach(rotorsOption -> {
                                                allRotorIDOptions.addAll(Generator.permutation(rotorsOption)
                                                        .simple()
                                                        .stream()
                                                        .collect(Collectors.toList()));
                                            });
        }

        return allRotorIDOptions;
    }

    private List<List<Character>> getAllRotorStartPositionsOptions() {
        return Generator.permutation(machine.getAllKeys())
                .withRepetitions(machine.getActiveRotorsCount())
                .stream().collect(Collectors.toList());
    }

    private List<RomanNumber> getAllReflectorIDOptions() {
        List<RomanNumber> allReflectorIDOptions = new ArrayList<>();

        if (decryptionDifficulty.getIntValue() < 2) {
            allReflectorIDOptions.add(initialMachineConfiguration.getReflectorID());
        }
        else {
            IntStream.range(1, machine.getMachineStorage().getAllReflectors().size() + 1).boxed().collect(Collectors.toList()).forEach(reflectorIDNumber -> {
                allReflectorIDOptions.add(RomanNumber.fromInt(reflectorIDNumber));
            });
        }

        return  allReflectorIDOptions;
    }

    @Override
    public void run() {
        int allRotorStartPositionsOptionsAmount = allRotorStartPositionsOptions.size();
        MachineConfiguration startConfiguration = null;
        MachineConfiguration endConfiguration = null;

        for (List<Integer> currRotorIDOption : allRotorIDOptions) {
            for (RomanNumber currReflectorIDOption : allReflectorIDOptions) {
                for (int i = 0; i < allRotorStartPositionsOptionsAmount; i += tasksSize) {
                    int endTaskSize = i;

                    for (int j = i; j < i + tasksSize && j < allRotorStartPositionsOptionsAmount; j++) {
                        List<Character> currRotorStartPositionsOption = allRotorStartPositionsOptions.get(j);

                        if (j == i) {
                            startConfiguration = new MachineConfiguration(currRotorIDOption, currRotorStartPositionsOption, currReflectorIDOption);
                        } else if (j == i + tasksSize - 1 || j == allRotorStartPositionsOptionsAmount - 1) {
                            endConfiguration = new MachineConfiguration(currRotorIDOption, currRotorStartPositionsOption, currReflectorIDOption);
                            endTaskSize = j;
                        }
                    }

                    tasksQueue.add(new DecryptionTaskDetails(startConfiguration, endConfiguration, endTaskSize - i));
                    onTaskCreated.run();
                }
            }
        }
    }
}
