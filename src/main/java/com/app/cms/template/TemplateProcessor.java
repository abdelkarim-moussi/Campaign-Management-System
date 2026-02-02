package com.app.cms.template;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class TemplateProcessor {
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{(\\w+)\\}\\}");

    public List<String> extractVariables(String text){
        List<String> variables = new ArrayList<>();

        Matcher matcher = VARIABLE_PATTERN.matcher(text);

        while(matcher.find()){
            String variable = matcher.group(1);

            if(!variables.contains(variable)){
                variables.add(variable);
            }
        }

        return variables;
    }



}
