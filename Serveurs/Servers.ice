module Demo
{
    struct Song {
        int rowid;
        string titre;
        string artiste;
        string album;
        int duration;
        string path;
    };

    sequence<Song> SongList;
    sequence<byte> ByteData;

    interface Serveurs
    {
        SongList getAllSongs();
    }

    interface VLC
    {
        void sayHelloVLC();
        void playStream();
        void pauseStream();
        void stopStream();
        void playSong(int id);
    }

    interface ASR
    {
        string recognize(ByteData speech);
    }

    interface NLP
    {
        string process(string text);
    }
}