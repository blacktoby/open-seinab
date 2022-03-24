package de.seinab.form;

import de.seinab.form.entities.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InputUtils {

    public static List<InputValue> sortInputValuesByPositionAndGroup(List<InputValue>  inputValueList) {
        List<Input> inputList = inputValueList.stream().map(InputValue::getInput).collect(Collectors.toList());
        inputList = sortInputListByPositonAndGroup(inputList);

        Map<Long, InputValue> inputValueByInputId =
                inputValueList.stream().collect(Collectors.toMap(inputValue -> inputValue.getInput().getId(), iv -> iv));
        return inputList.stream().map(input -> inputValueByInputId.get(input.getId())).collect(Collectors.toList());
    }

    public static List<Input> sortInputListByPositonAndGroup(List<Input> inputList) {
        inputList.sort(Comparator.comparing(i -> i.getInputGroup() == null ? i.getPosition() : i.getInputGroup().getPosition()));

        Long currentInputGroup = (long) -1;
        List<Input> inputsSubGroupList = new ArrayList<>();
        List<Input> sortedList = new ArrayList<>();
        for(Input input : inputList) {
            InputGroup inputGroup = input.getInputGroup();
            if(inputGroup == null) {
                if(currentInputGroup > -1) {
                    sortAddClearGroupListInto(sortedList, inputsSubGroupList);
                    currentInputGroup = (long) -1;
                }
                sortedList.add(input);
                continue;
            }

            if(!currentInputGroup.equals(inputGroup.getId())) {
                sortAddClearGroupListInto(sortedList, inputsSubGroupList);
                currentInputGroup = inputGroup.getId();
            }
            inputsSubGroupList.add(input);
        }

        if(!inputsSubGroupList.isEmpty()) {
            sortAddClearGroupListInto(sortedList, inputsSubGroupList);
        }
        return sortedList;
    }

    private static void sortAddClearGroupListInto(List<Input> sortedList, List<Input> inputsSubGroupList) {
        inputsSubGroupList.sort(Comparator.comparingInt(Input::getPosition));
        sortedList.addAll(inputsSubGroupList);
        inputsSubGroupList.clear();
    }

    public static String beautifyInputValueData(InputValue inputValue) {
        Input input = inputValue.getInput();
        String value = inputValue.getData();
        if(input.getType() == InputType.select) {
            String finalValue = value;
            value = ((SelectInput) input).getInputOptions()
                    .stream()
                    .filter(inputOption -> StringUtils.equals(inputOption.getValue(), finalValue))
                    .map(InputOption::getText)
                    .findFirst().orElse("");
        } else if(input.getType() == InputType.checkbox) {
            if(StringUtils.equals(inputValue.getData(), "true")) {
                value = "Ja";
            } else {
                value = "Nein";
            }
        }
        return value;
    }
}
