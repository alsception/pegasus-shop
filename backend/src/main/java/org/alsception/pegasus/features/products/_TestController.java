package org.alsception.pegasus.features.products;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class _TestController 
{
    private static final Logger logger = LoggerFactory.getLogger(_TestController.class);

    @GetMapping("/test-write-error")
    public void simulateSerializationError() {
        logger.debug("test-write-err");
        throw new HttpMessageNotWritableException("Simulated error");
    }

    @GetMapping("/test-circular")
    public _WeirdDTO getWeird() 
    {
        logger.info("test-circular");
        return new _WeirdDTO();
    }
}