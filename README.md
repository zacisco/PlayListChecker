# PlayListChecker
Simple Application for checking m3u playlists (channel availability)

Multithreaded

**Requirements**

* ffmpeg! (ffprobe)

**Information**

just RUN application and get help

**Some Description**

*folders:*
* lists - contain *txt* files with urls to m3u files
  * General
  * Movies
  * Serials
  * World
  * XXX
* custom - contain folders with ready m3u files for scan
  * General
  * Movies
  * Serials
  * World
  * XXX

**About Groups**

All channels from *Movies*, *Serials*, *World*, *XXX* will be added to the same group.

Channels from *General* group will be check with **channelGroups.txt** and if file will not contain group, then channel will be added in **Other** group.
