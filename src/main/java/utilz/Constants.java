package utilz;

public class Constants {

    public static class Directions{
        public static final int LEFT = 0;
        public static final int UP = 1;
        public static final int RIGHT = 2;
        public static final int DOWN = 3;
    }
    public static class PlayerConstants{
        public static final int IDLE = 0;
        public static final int RUNNING = 1;
        public static final int JUMPING = 0;
        public static final int HIT = 6;
        

        public static int GetSpriteAmount (int player_action){
            switch (player_action) {
                case RUNNING:
                    return 6;
                case IDLE:
                    return 7;
                case HIT:
                    return 2;
                default:
                    return 1;
            }
        }
    }
}
