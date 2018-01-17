UPDATE portti_bundle SET startup='{
    "title": "Download basket",
    "bundleinstancename": "download-basket",
    "bundlename": "download-basket",
    "metadata": {
        "Import-Bundle": {
            "download-basket": {
                "bundlePath": "/Oskari/packages/framework/bundle/"
            }
        }
    }
}'
WHERE name = 'download-basket'