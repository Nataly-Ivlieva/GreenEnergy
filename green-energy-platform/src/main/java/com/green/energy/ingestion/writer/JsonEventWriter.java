package com.green.energy.ingestion.writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.energy.ingestion.event.HistoricalEvent;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class JsonEventWriter {

    public static final Path OUTPUT =
            Path.of(System.getProperty("user.dir"))
                    .getParent()
                    .resolve("data")
                    .resolve("historical_events.jsonl");

    private final BufferedWriter writer;
    private final ObjectMapper mapper =
            new ObjectMapper().findAndRegisterModules();

    public JsonEventWriter() throws IOException {

        Files.createDirectories(OUTPUT.getParent());
        this.writer = Files.newBufferedWriter(OUTPUT);
    }

    public synchronized void write(HistoricalEvent event) throws IOException {
        System.out.println(event);
        writer.write(mapper.writeValueAsString(event));
        writer.newLine();
    }

    public void close() throws IOException {
        writer.close();
    }
}
