package com.acme.server;

import com.acme.commons.application.Context;
import com.acme.commons.application.ContextBuilder;
import com.acme.commons.ashley.WiringEngine;
import com.acme.server.entity.Archetypes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Launcher {

    public static void main(String[] args) throws Exception {
        Context context = new ContextBuilder(new BrowserQuest())
                .setApplicationName(BrowserQuest.class.getSimpleName())
                .setUpdateInterval(1000 / 60)
                .build();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String s;
            while ((s = readLine(br)) != null) {
                if (s.startsWith("exit")) {
                    context.dispose();
                    break;
                } else if (s.startsWith("players")) {
                    System.out.println(context.get(WiringEngine.class).
                            getEntitiesFor(Archetypes.PLAYER_TYPE.getFamily())
                            .toString("\r\n"));
                }
            }
        }
    }

    private static String readLine(BufferedReader br) throws IOException {
        System.out.print("> ");
        return br.readLine();
    }
}
