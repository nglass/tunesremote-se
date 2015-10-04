# PlaylistControls #

Here I will be adding information about controlling playlists with DACP.

## Remote ##
Playlist controls include:
  * add - add songs to playlist
  * new - create a new genius based on a song selection
  * refresh - regenerate the current genius list
  * edit - switches to edit mode to allow reordering, removal of songs and on the main genius list allows you to save the genius list

The type of playlist you are viewing determines the controls available to you:
  * regular playlists can edit/add
  * smart playlists can edit
  * main genius list can edit/new/refresh
  * saved genius lists can edit/refresh

## Protocol ##

**Add Playlist**

`GET /databases/64/edit?action=add&edit-params='dmap.itemname:New%20Playlist'&session-id=556874004`

iTunes returns the id of the new playlist in a medc structure.

```
medc  --+
        mstt   4      200
        miid   4      28770
```

**Rename Playlist**

`GET /databases/64/containers/68612/edit?action=rename&edit-params='dmap.itemname:Renamed%20Playlist'&session-id=706060394`

**Delete Playlist**

`GET /databases/64/edit?action=remove&edit-params='dmap.itemid:68612'&session-id=706060394`

**Add Single Track**

`GET /databases/64/containers/28770/edit?action=add&edit-params='dmap.itemid:6228'&session-id=2057152154`

once again we get a medc structure back
```
medc  --+
        mstt   4      200
        mlit  --+
                mcti    4    28773
```
mcti tells us the new container-item-id of the track in the playlist.

**Add Multiple Tracks**

To add multiple tracks it would appear the remote selects using a standard daap query.

`GET /databases/65/containers/21867/edit?action=add&edit-params=(('daap.songartist:Weezer','daap.songalbumartist:Weezer')+('com.apple.itunes.mediakind:1','com.apple.itunes.mediakind:32'))&session-id=1420969565`

**Remove Single Track**

`GET /databases/64/containers/21867/edit?action=remove&edit-params='dmap.containeritemid:21865'&session-id=2057152154`

**Move Track**

`GET /databases/65/containers/19071/edit?action=move&edit-params='edit-param.move-pair:24804,24801'&session-id=1860779006`

**Genius**

`GET /ctrl-int/1/set-genius-seed?database-spec='dmap.persistentid:0xBBD273CF5AE2139E'&item-spec='dmap.itemid:0x2362'&session-id=1860779006`

**Refresh**

`GET /databases/65/containers/19071/edit?action=refresh&session-id=1641444128`