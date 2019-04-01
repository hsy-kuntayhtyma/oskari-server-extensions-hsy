-- update resources
UPDATE oskari_resource
SET resource_mapping = replace(resource_mapping, 'http://kartta.hsy.fi', 'https://kartta.hsy.fi')
WHERE resource_mapping LIKE '%http://kartta.hsy.fi%'
  AND
        replace(resource_mapping, 'http://kartta.hsy.fi', 'https://kartta.hsy.fi') NOT IN (SELECT resource_mapping FROM oskari_resource
                                                                                           WHERE resource_mapping LIKE '%https://kartta.hsy.fi%');

-- update layer urls
UPDATE oskari_maplayer
SET url = replace(url, 'http://kartta.hsy.fi', 'https://kartta.hsy.fi')
WHERE url LIKE '%http://kartta.hsy.fi%';

-- remove old resources
DELETE FROM oskari_resource WHERE  resource_mapping LIKE '%http://kartta.hsy.fi%';