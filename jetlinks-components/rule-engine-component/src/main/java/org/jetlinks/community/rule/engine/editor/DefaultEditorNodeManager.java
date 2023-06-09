package org.jetlinks.community.rule.engine.editor;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@AllArgsConstructor
public class DefaultEditorNodeManager implements EditorNodeManager {

    private final ObjectProvider<EditorNodeProvider> providers;

    @Override
    public Flux<EditorNode> getNodes() {
        return Flux
            .fromIterable(providers)
            .flatMap(EditorNodeProvider::getNodes);
    }


}
