import sqlite3
import pandas as pd

def setupDB():
      conn = sqlite3.connect('soup_database.db') 
      c = conn.cursor()

      c.execute('''
            CREATE TABLE IF NOT EXISTS tracks
            ([titre] TEXT, [artiste] TEXT, [album] TEXT, [duration] INTEGER, [path] TEXT)
            ''')
                        
      c.execute('''
            INSERT INTO tracks (titre, artiste, album, duration, path)

                  VALUES
                  ('Tarkov', 'Freeze Corleone', 'LMF', 230, 'tracks/tarkov.mp3')
            ''')

      c.execute('''
            INSERT INTO tracks (titre, artiste, album, duration, path)

                  VALUES
                  ('Chen Laden', 'Freeze Corleone', 'LMF', 199, 'tracks/17-Chen-Laden.mp3')
            ''')
      
      c.execute('''
            INSERT INTO tracks (titre, artiste, album, duration, path)

                  VALUES
                  ('Ambitionz Az a Ridah', 'Tupac', 'All Eyez On Me', 199, 'tracks/Ambitionz.mp3')
            ''')
      
      c.execute('''
            INSERT INTO tracks (titre, artiste, album, duration, path)

                  VALUES
                  ('Something In The Way', 'Nirvana', 'Nevermind', 230, 'tracks/something-in-the-way.mp3')
            ''')

      conn.commit()

def main():
      setupDB()

def getAll():
      conn = sqlite3.connect("soup_database.db") 
      c = conn.cursor()
      c.execute('''
            SELECT rowid, titre, artiste, album, duration, path from tracks
            ''')
      return c.fetchall()

def getTrackPath(id):
      conn = sqlite3.connect("soup_database.db") 
      c = conn.cursor()
      c.execute("select path from tracks where rowid = ?", (id,))
      df = pd.DataFrame(c.fetchall(), columns=(['path']))
      return df.iloc[0]['path']     

if __name__ == '__main__':
        main()
        s = getAll("soup_database.db")
        for c in s:
              print(c)