package app.model;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class GroupsList {
    public static HashSet<Long> getGroups() {
        HashSet<Long> chats = new HashSet<>();
        chats.add(-1001539493710L);
        chats.add(-1001263087493L);
        chats.add(-1001619476206L);
        chats.add(-1001576214907L);
        chats.add(-1001694198399L);
        chats.add(-1001676417761L);
        chats.add(-1001500647848L);
        chats.add(-1001276921042L);
        chats.add(-1001456248131L);
        chats.add(-1001581625978L);
        chats.add(-1001340204117L);
        chats.add(-1001466983272L);
        chats.add(-1001790634030L);
        chats.add(-1001881647194L);
        chats.add(-1001360878999L);
        return chats;
    }
}
