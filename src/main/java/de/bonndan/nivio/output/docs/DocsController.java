package de.bonndan.nivio.output.docs;

import com.github.fluorumlabs.asciidocj.AsciiDocument;
import de.bonndan.nivio.landscape.Landscape;
import de.bonndan.nivio.landscape.LandscapeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;

@Controller
@RequestMapping(path = "/docs")
public class DocsController {

    private final LandscapeRepository landscapeRepository;

    @Autowired
    public DocsController(LandscapeRepository landscapeRepository) {
        this.landscapeRepository = landscapeRepository;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{landscape}")
    public ResponseEntity<String> docResource(@PathVariable(name = "landscape") final String landscapeIdentifier) throws IOException {
        Landscape landscape = landscapeRepository.findDistinctByIdentifier(landscapeIdentifier);
        if (landscape == null)
            throw new EntityNotFoundException("Not found");

        AsciiDocGenerator generator = new AsciiDocGenerator();
        AsciiDocument parsedAsciidoc = AsciiDocument.from(generator.toDocument(landscape));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/html");
        return new ResponseEntity<>(
                parsedAsciidoc.getHtml(),
                headers,
                HttpStatus.OK
        );

    }


}
