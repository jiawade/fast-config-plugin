package com.fast.config;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TSArrayConverter {

    public String[] getJavaArray(File configFile) {
        Set<String> matched = new HashSet<>();
        boolean matches = false;

        try {
            List<String> lines = FileUtils.readLines(configFile, "UTF-8");
            for (String line : lines) {
                if (line.contains("export const all =")) {
                    matched.add(line);
                    if (line.contains(";")) break;
                    matches = true;
                }
                if (matches) {
                    matched.add(line);
                    if (line.contains(";")) break;
                }
            }

            String allMatches = String.join(", ", matched);
            Pattern pattern = Pattern.compile("'(.*?)'");
            Matcher matcher = pattern.matcher(allMatches);

            List<String> result = new ArrayList<>();

            // 查找所有匹配的字符串并添加到结果列表
            while (matcher.find()) {
                result.add(matcher.group(1));
            }


            return result.toArray(new String[0]);
    } catch (Exception e) {
        e.printStackTrace();
    }
        return null;
    }
}
