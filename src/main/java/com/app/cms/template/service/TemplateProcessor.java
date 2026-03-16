package com.app.cms.template.service;

import lombok.extern.slf4jvn .Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class TemplateProcessor {
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{(\\w+)}}");

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

    public String processTemplate(String template, Map<String,String> variables){
        if(template == null || template.isEmpty()){
            return null;
        }

        String result = template;

        for (Map.Entry<String,String> entry : variables.entrySet()){
            String placeholder = "{{"+ entry.getKey() + "}}";
            String value = entry.getValue() ;
            result = result.replace(placeholder,value);
        }

        Matcher matcher = VARIABLE_PATTERN.matcher(result);
        if(matcher.find()){
            log.warn("Template has unreplaced variables {} ", result);
        }

        return result;
    }

    public boolean validateVariables(String template, Map<String,String> providedVariables){
        List<String> requiredVariables = extractVariables(template);

        for(String variable : requiredVariables){
            if(!providedVariables.containsKey(variable)){
                log.error("Missing variable {} ", variable);
                return false;
            }
        }

        return true;
    }

}
