-- update layer urls
UPDATE oskari_maplayer
SET url = REPLACE(url, 'http://localhost:8080', 'https://kartta.hsy.fi')
WHERE url LIKE '%http://localhost:8080%'
