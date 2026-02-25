package com.app.cms.template;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/templates")
@RequiredArgsConstructor
public class TemplateController {
    private final TemplateService templateService;

    @PostMapping
    public ResponseEntity<Template> createTemplate(@Valid @RequestBody TemplateDTO dto){
        Template created = templateService.createTemplate(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Template> getTemplate(@PathVariable String id){
        Template template = this.templateService.getTemplate(id);
        return ResponseEntity.ok(template);
    }

    @GetMapping
    public ResponseEntity<List<Template>> getAllTemplates(
            @RequestParam(required = false) TemplateType type,
            @RequestParam(required = false) TemplateStatus status){

        if(type != null && status != null){
            return ResponseEntity.ok(
                    templateService.getTemplatesByStatus(status)
                            .stream()
                            .filter(t -> t.getType().equals(type))
                            .toList()
            );
        }else if(type != null){
            return ResponseEntity.ok(templateService.getTemplatesByType(type));
        }else if(status != null){
            return ResponseEntity.ok(templateService.getTemplatesByStatus(status));
        }

        return ResponseEntity.ok(this.templateService.getAllTemplates());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Template> updateTemplate(@PathVariable String id, @RequestBody @Valid TemplateDTO request){
        Template updatedTemplate = this.templateService.updateTemplate(id,request);
        return ResponseEntity.ok(updatedTemplate);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Template>> getActiveTemplates(@RequestParam TemplateStatus status){
        return ResponseEntity.ok(this.templateService.getTemplatesByStatus(status));
    }

    @GetMapping("/{id}/variables")
    public ResponseEntity<List<String>> getTemplateVariables(@PathVariable String id){
        return ResponseEntity.ok(
                templateService.getTemplateVariables(id)
        );
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Template> activateTemplate(@PathVariable String id){
        return ResponseEntity.ok(
                templateService.activateTemplate(id)
        );
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Template> archiveTemplate(@PathVariable String id){
        return ResponseEntity.ok(
                templateService.archiveTemplate(id)
        );
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<Template>> searchTemplates(@PathVariable String keyword){
        return ResponseEntity.ok(this.templateService.searchTemplates(keyword));
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<TemplatePreviewResult> previewTemplate(
            @PathVariable String id,
            @RequestBody Map<String,String> variables){
        return ResponseEntity.ok(
                templateService.previewTemplate(id,variables)
        );
    }
}
