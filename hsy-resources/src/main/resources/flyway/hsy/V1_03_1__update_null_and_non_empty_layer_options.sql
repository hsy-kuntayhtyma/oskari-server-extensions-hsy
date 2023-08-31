UPDATE oskari_maplayer
SET options = '{}'
WHERE options IS NULL OR options = '';
