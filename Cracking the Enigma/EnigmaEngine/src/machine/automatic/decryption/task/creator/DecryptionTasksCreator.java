package machine.automatic.decryption.task.creator;

import machine.Machine;
import machine.automatic.decryption.difficulty.DecryptionDifficulty;
import machine.automatic.decryption.input.data.DecryptionInputData;
import machine.automatic.decryption.task.DecryptionTask;
import machine.automatic.decryption.task.results.DecryptionTaskResults;
import object.machine.configuration.MachineConfiguration;
import object.numbering.RomanNumber;
import org.paukov.combinatorics3.Generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DecryptionTasksCreator extends Thread {
    private final MachineConfiguration startingMachineConfiguration;
    private final BlockingQueue<Runnable> tasksQueue;
    private final BlockingQueue<DecryptionTaskResults> resultsQueue;
    private final DecryptionDifficulty decryptionDifficulty;
    private final String messageToDecrypt;
    private final Machine machineCopy;
    private final int tasksSize;
    private final List<List<Integer>> allRotorIDOptions;
    private final List<List<Character>> allRotorStartPositionsOptions;
    private final List<RomanNumber> allReflectorIDOptions;
    private final Map<Integer, Machine> agentIDToMachine;
    private final Runnable checkIfPaused;
    private final Predicate<List<String>> areWordsInDictionary;

    public DecryptionTasksCreator(DecryptionInputData decryptionInputData, BlockingQueue<Runnable> tasksQueue,
                                  BlockingQueue<DecryptionTaskResults> resultsQueue, Machine machineCopy,
                                  Map<Integer, Machine> agentIDToMachine, Runnable checkIfPaused) {
        setName("Decryption Task Creator");
        setDaemon(true);
        this.startingMachineConfiguration = machineCopy.getInitialMachineConfiguration();
        this.tasksQueue = tasksQueue;
        this.resultsQueue = resultsQueue;
        this.decryptionDifficulty = decryptionInputData.getDecryptionDifficulty();
        this.messageToDecrypt = decryptionInputData.getMessageToDecrypt();
        this.machineCopy = machineCopy;
        this.tasksSize = decryptionInputData.getTasksSize();
        allRotorIDOptions = getAllRotorIDOptions();
        allRotorStartPositionsOptions = getAllRotorStartPositionsOptions();
        allReflectorIDOptions = getAllReflectorIDOptions();
        this.agentIDToMachine = agentIDToMachine;
        this.checkIfPaused = checkIfPaused;
        areWordsInDictionary = machineCopy.getDictionary()::areWordsInDictionary;
    }

    public int getAmountOfTotalTasks() {
        return allRotorIDOptions.size() * allRotorStartPositionsOptions.size() * allReflectorIDOptions.size() / tasksSize;
    }

    private List<List<Integer>> getAllRotorIDOptions() {
        List<List<Integer>> allRotorIDOptions = new ArrayList<>();

        if (decryptionDifficulty.getIntValue() <= 2) {
            allRotorIDOptions.add(startingMachineConfiguration.getRotorIDsInOrder());
        } else if (decryptionDifficulty.getIntValue() == 3) {
            allRotorIDOptions.addAll(Generator.permutation(startingMachineConfiguration.getRotorIDsInOrder())
                    .simple()
                    .stream()
                    .collect(Collectors.toList()));
        } else  {
            Generator.combination(IntStream.range(1, machineCopy.getMachineStorage().getAllRotors().size() + 1).boxed().collect(Collectors.toList()))
                            .simple(machineCopy.getActiveRotorsCount())
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
        return Generator.permutation(machineCopy.getAllKeys())
                .withRepetitions(machineCopy.getActiveRotorsCount())
                .stream().collect(Collectors.toList());
    }

    private List<RomanNumber> getAllReflectorIDOptions() {
        List<RomanNumber> allReflectorIDOptions = new ArrayList<>();

        if (decryptionDifficulty.getIntValue() < 2) {
            allReflectorIDOptions.add(startingMachineConfiguration.getReflectorID());
        }
        else {
            IntStream.range(1, machineCopy.getMachineStorage().getAllReflectors().size() + 1).boxed().collect(Collectors.toList()).forEach(reflectorIDNumber -> {
                allReflectorIDOptions.add(RomanNumber.fromInt(reflectorIDNumber));
            });
        }

        return  allReflectorIDOptions;
    }

    @Override
    public void run() {
        for (List<Integer> currRotorIDOption : allRotorIDOptions) {
            for (RomanNumber currReflectorIDOption : allReflectorIDOptions) {
                for (int i = 0; i < getAllRotorStartPositionsOptions().size(); i += tasksSize) {
                    List<MachineConfiguration> machineConfigurationsToTestList = new ArrayList<>();

                    for (int j = i; j < i + tasksSize && j < getAllRotorStartPositionsOptions().size(); j++) {
                        List<Character> currRotorStartPositionsOption = allRotorStartPositionsOptions.get(j);

                        machineConfigurationsToTestList.add(new MachineConfiguration(currRotorIDOption, currRotorStartPositionsOption, currReflectorIDOption));
                    }

                    tasksQueue.add(new DecryptionTask(areWordsInDictionary, messageToDecrypt, machineConfigurationsToTestList, resultsQueue, agentIDToMachine, checkIfPaused));

                    if (isInterrupted()) {
                        return;
                    }
                }
            }
        }
    }
}
