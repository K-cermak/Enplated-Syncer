<?php
    error_reporting(E_ALL);
    ini_set('display_errors', 1);
    echo substr(sprintf('%o', fileperms('/data/web/virtuals/219461/virtual/www/subdom/enplated-syncer/index.html')), -4);

    $files1 = scandir("/data/web/virtuals/219461/virtual/www/subdom/enplated-syncer/", 1);
    print_r($files1);
?>