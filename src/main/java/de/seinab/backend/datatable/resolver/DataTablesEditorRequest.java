package de.seinab.backend.datatable.resolver;

import org.apache.commons.beanutils.BeanUtilsBean2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataTablesEditorRequest {
    private static final Logger log = LoggerFactory.getLogger(DataTablesEditorRequest.class);

    private Map<String, String[]> parameterMap;
    private BeanUtilsBean2 beanUtils = new BeanUtilsBean2();

    public DataTablesEditorRequest(Map<String, String[]> parameterMap) {
        this.parameterMap = parameterMap;
    }

    public String getAction() {
        return parameterMap.get("action")[0];
    }

    public Map<String, String> getParameterAsRawDataTablesMap() {
        Map<String, String> tableParameterMap = new HashMap<>();
        parameterMap.forEach((key, value) -> {
            if (key.contains("data")) {
                tableParameterMap.put(key, value[0]);
            }
        });
        return tableParameterMap;
    }

    /**
     * Puts the data sent from Datatables into a Map with key and value.<br>
     * <br>
     * Example:<br>
     * data[10][articleId]:	1009<br>
     * data[10][color]:	danger<br>
     * data[11][articleId]: 1008<br>
     * data[11][color]: info<br>
     * <br>
     * Will be:<br>
     * [{id: "10",<br>
     * articleId: "10009",<br>
     * color: "danger"},<br>
     * {id: "11",<br>
     * articleId: "10008",<br>
     * color: "info"}]<br>
     * <br>
     * Where [] is a List and {} a Map.<br>
     */
    public List<Map<String, String>> getParameterList() {
        Map<String, Map<String, String>> groupMap = new HashMap<>();
        getParameterAsRawDataTablesMap().forEach((key, value) -> parseParameter(groupMap, key, value));
        return new ArrayList<>(groupMap.values());
    }

    // old fashioned parsing, regex doesn't work because of german "umlaute" and whitespace (regex = old shit)
    // may be a bit ugly but works 100 % (good old fashioned way)
    private void parseParameter(Map<String, Map<String, String>> groupMap, String key, String value) {
        StringBuilder group = new StringBuilder();
        StringBuilder dataKey = new StringBuilder();
        StringCharacterIterator iterator = new StringCharacterIterator(key);
        char currentChar;
        // searching open character '['
        while((currentChar = iterator.next()) != StringCharacterIterator.DONE && currentChar != '[') {}
        // save everything until ']' into "group"
        while((currentChar = iterator.next()) != StringCharacterIterator.DONE && currentChar != ']') {
            group.append(currentChar);
        }
        // searchung open character '['
        while((currentChar = iterator.next()) != StringCharacterIterator.DONE && currentChar != '[') {}
        // saving everything until ']' into "dataKey"
        while((currentChar = iterator.next()) != StringCharacterIterator.DONE && currentChar != ']') {
            dataKey.append(currentChar);
        }
        // ignore everything else, because not needed!

        addDataToGroupMap(groupMap, group.toString(), dataKey.toString(), value);
    }

    private void addDataToGroupMap(Map<String, Map<String, String>> groupMap, String group, String dataKey, String dataValue) {
        groupMap.computeIfAbsent(group, key -> new HashMap<>());
        groupMap.get(group).putIfAbsent("id", group);
        groupMap.get(group).put(dataKey, dataValue);
    }


    /**
     * <p>Maps the data sent from Datatable to a Object of the given classType.</p>
     * <br>
     * <p>Example:</p>
     * <p>data[10][articleId]:	1009</p>
     * <p>data[10][color]:	danger</p>
     * <p>"articleId" and "color" will be mapped to member of class with same name.</p>
     */
    public <T> List<T> getObjectList(Class<T> objectMapperClass) {
        List<T> objectList = new ArrayList<>();
        getParameterList().forEach(group -> {
            log.debug(group.toString());
            T object = createObject(objectMapperClass);
            group.forEach((key, value) -> setProperty(object, key, value));
            objectList.add(object);
        });
        return objectList;
    }

    private <T> void setProperty(T object, String key, String value) {
        try {
            beanUtils.setProperty(object, key, value);
        } catch (Exception e) {
            //If not setable, skip.
            log.warn(e.getMessage());
        }
    }

    private <T> T createObject(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
