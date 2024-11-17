package org;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InterviewClass {
    public static void main(String[] args) {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("Hello, World!");
        strings.add("Welcome to CoderPad.");
        strings.add("This pad is running Java " + Runtime.version().feature());

        List<String> stringList = strings.stream()
            .filter(n -> n.length() > 20)
            .collect(Collectors.toList());
        for (String string : stringList) {
            System.out.println(string);
        }
    }

}
