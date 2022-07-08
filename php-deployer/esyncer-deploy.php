<?php
    header('Content-Type: text/html; charset=utf-8');
    session_start();
    require_once "esyncer-vars.php";
?>

<?php            
    //***********************************************************
    //                    UPLOAD FILE SYSTEM
    // - 200 - ok
    // - 400 - error
    //***********************************************************

    if ($_SERVER['REQUEST_METHOD'] == "POST" && isset($_GET["deploy"])) {
        http_response_code(400);
        do {
            $pathForExtract = "extract/";
            //check lock
            if (file_exists($pathForExtract . "esyncer.lock")) {
                echo "error-locked";
                http_response_code(400);
                break;
            } else {
                //create file
                file_put_contents($pathForExtract . "esyncer.lock", "");
            }

            //verify that token exist
            if (!isset($_GET["token"]) || !isset($_GET["secret"])) {
                echo "missing-token-or-secret";
                http_response_code(400);
                break;
            }

            //verify that token is correct
            $token = $_GET["token"];
            $secret = $_GET["secret"];
            $project = null;

            for ($i = 0; $i < count(PROJECTS); $i++) {
                if (PROJECTS[$i]["token"] == $token && PROJECTS[$i]["secret"] == $secret) {
                    $project = PROJECTS[$i];
                    break;
                }
            }

            if ($project == null) {
                echo "invalid-token-or-secret";
                http_response_code(400);
                break;
            }

            if (!isset($_FILES["file"])) {
                echo "missing-file";
                http_response_code(400);
                break;
            }

            //save file to folder
            $file = $_FILES["file"];
            $file_name = $file["name"];
            $file_tmp_name = $file["tmp_name"];
            $file_size = $file["size"];
            $file_error = $file["error"];

            if ($file_error != 0) {
                echo "file-error";
                http_response_code(400);
                break;
            }

            //check if zip
            $file_ext = explode(".", $file_name);
            $file_ext = strtolower(end($file_ext));
            if ($file_ext != "zip") {
                echo "invalid-file-extension";
                http_response_code(400);
                break;
            }

            //save
            $file_path = $pathForExtract . $file_name;
            if (!move_uploaded_file($file_tmp_name, $file_path)) {
                echo "file-error";
                http_response_code(400);
                break;
            }

            //delete content of folder
            $dir = $project["folder"];
            delete($dir, $dir);

            //unzip file to folder
            $zip = new ZipArchive;
            $res = $zip->open($file_path);
            if ($res === TRUE) {
                $zip->extractTo($pathForExtract);
                $zip->close();
            } else {
                echo "file-error";
                http_response_code(400);
                break;
            }

            //get all folders in /extract/
            $dir = $pathForExtract;
            $files = scandir($dir);
            $folders = [];
            foreach ($files as $file) {
                if (is_dir($dir . $file)) {
                    if ($file != "." && $file != "..") {
                        $folders[] = $file;
                    }
                }
            }

            //open first index from folders
            $dir = $pathForExtract . $folders[0];

            //move all files from /extract/ to /project/
            recurse_copy($dir, $project["folder"]);

            //delete unzip folder
            delete($pathForExtract, $pathForExtract);

            echo "ok-done";
            http_response_code(200);
            
        } while (false);

        //delete lock
        if (file_exists($pathForExtract . "esyncer.lock")) {
            unlink($pathForExtract . "esyncer.lock");
        }

        exit();
    }

    //functions
    function delete($dir, $originalFolder) {
        if (is_dir($dir)) {
            $objects = scandir($dir);
            foreach ($objects as $object) {
                if ($object != "." && $object != "..") {
                    if (filetype($dir."/".$object) == "dir") {
                        delete($dir."/".$object, $dir);
                    } else {
                        unlink($dir."/".$object);
                    }
                }
            }
            reset($objects);
            if ($dir != $originalFolder) {
                rmdir($dir);
            }
        }
    }

    function recurse_copy($src, $dst) { 
        $dir = opendir($src); 
        @mkdir($dst); 
        while(false !== ( $file = readdir($dir)) ) { 
            if (( $file != '.' ) && ( $file != '..' )) { 
                if ( is_dir($src . '/' . $file) ) { 
                    recurse_copy($src . '/' . $file,$dst . '/' . $file); 
                } 
                else { 
                    copy($src . '/' . $file,$dst . '/' . $file); 
                } 
            } 
        } 
        closedir($dir); 
    }

?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Enplated Syncer - Deploy</title>

    <meta name="color-scheme" content="light dark">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-dark-5@1.1.3/dist/css/bootstrap-nightfall.min.css" rel="stylesheet" media="(prefers-color-scheme: light)">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
</head>
<body>
    <style>
        .blur {
            filter: blur(5px);
            transition: 0.5s;
            padding: 0px 20px 0px 10px;
        }
        .blur:hover {
            filter: blur(0px);
        }
        .navbar-collapse {
            justify-content: center;
        }
    </style>

<?php


        //***********************************************************
        //                     ROUTING SYSTEM
        //***********************************************************
        do {
            //new install
            if (!$_POST && ACCESS_CODE == "") {
                echo generateWelcome();
                break;
            }

            //new install code
            if (isset($_GET["new-access"]) && isset($_POST["new-access-code"])) {
                //verify
                if ($_POST["new-access-code"] == $_POST["new-access-code-repeat"] && ACCESS_CODE == "") {
                    //min 5 chars
                    if (strlen($_POST["new-access-code"]) >= 5) {
                        replaceLine(2, 'define("ACCESS_CODE", "' . $_POST["new-access-code"] . '");');
                        echo generateMessage("success", "Access code updated! Please login.");
                    } else {
                        echo generateMessage("danger", "Access code must be at least 5 characters long.");
                        echo generateWelcome();
                        break;
                    }
                } else {
                    echo generateMessage("danger", "Access code mismatch!");
                    echo generateWelcome();
                    break;
                }
            }
            
            //logout
            if (isset($_GET["logout"]) && $_SESSION["access-code"] == ACCESS_CODE) {
                session_destroy();
                header("Location: esyncer-deploy.php");
                break;
            }

            //auto login
            if (isset($_SESSION["access-code"]) && $_SESSION["access-code"] == ACCESS_CODE) {
                echo generateLoggedHeader();
            }

            //login
            if (isset($_GET["login"]) && $_POST) {
                if (isset($_POST["access-code"]) && $_POST["access-code"] == ACCESS_CODE) {
                    $_SESSION["access-code"] = ACCESS_CODE;
                    $_SESSION["message"] = "Login successful";
                    $_SESSION["message-type"] = "success";
                    header("Location: esyncer-deploy.php");
                    die();
                } else {
                    echo generateMessage("danger", "Access code is incorrect.");
                }
            }


            //change access code
            if (isset($_GET["change-access"]) && isset($_SESSION["access-code"]) && $_SESSION["access-code"] == ACCESS_CODE) {
                echo changeAccessCode();
                break;
            }

            if (isset($_GET["change-access-code"]) && isset($_SESSION["access-code"]) && $_SESSION["access-code"] == ACCESS_CODE) {
                //verify that old access code is correct
                if (isset($_POST["old-access-code"]) && $_POST["old-access-code"] == ACCESS_CODE) {
                    //verify that new access code is correct
                    if ($_POST["new-access-code"] == $_POST["new-access-code-repeat"]) {
                        //min 5 chars
                        if (strlen($_POST["new-access-code"]) >= 5) {
                            replaceLine(2, 'define("ACCESS_CODE", "' . $_POST["new-access-code"] . '");');
                            echo generateMessage("success", "Access code updated! <a href='?logout'>Logout</a> and login again to apply changes.");
                            break;
                        } else {
                            echo generateMessage("danger", "New Access code must be at least 5 characters long.");
                        }
                    } else {
                        echo generateMessage("danger", "New Access code mismatch.");
                    }
                } else {
                    echo generateMessage("danger", "Old Access code is incorrect.");
                }
                echo changeAccessCode();
                break;
            }


            //add a new project
            if (isset($_GET["add-folder"]) && isset($_SESSION["access-code"]) && $_SESSION["access-code"] == ACCESS_CODE) {
                echo addProject();
                break;
            }

            if (isset($_GET["add-folder-submit"]) && isset($_SESSION["access-code"]) && $_SESSION["access-code"] == ACCESS_CODE) {
                do {
                    //verify that project name is more than 3 chars
                    if (isset($_POST["project-name"]) && strlen($_POST["project-name"]) < 3) {
                        echo generateMessage("danger", "Project name must be at least 3 characters long.");
                        echo addProject($_POST["project-name"], $_POST["project-folder"]);
                        break 2;
                    }

                    //verify that project name is not already taken
                    for ($i = 0; $i < count(PROJECTS); $i++) {
                        if (PROJECTS[$i]["name"] == $_POST["project-name"]) {
                            echo generateMessage("danger", "Project name already exists.");
                            echo addProject($_POST["project-name"], $_POST["project-folder"]);
                            break 3;
                        }
                    }

                    //verify that folder is not empty
                    if (isset($_POST["project-folder"]) && strlen($_POST["project-folder"]) < 1) {
                        echo generateMessage("danger", "Project folder cannot be empty.");
                        echo addProject($_POST["project-name"], $_POST["project-folder"]);
                        break 2;
                    }

                    $token = "";
                    $tokenOrig = false;
                    do {
                        $token = generateRandom(8);
                        for ($i = 0; $i < count(PROJECTS); $i++) {
                            if (PROJECTS[$i]["token"] == $token) {
                                $tokenOrig = false;
                                break;
                            } else {
                                $tokenOrig = true;
                            }
                        }
                    } while ($tokenOrig == false && count(PROJECTS) != 0);

                    $secret = generateRandom(10);
                    $line = "        ['name' => '" . $_POST["project-name"] . "', 'folder' => '" . $_POST["project-folder"] . "', 'token' => '" . $token . "', 'secret' => '" . $secret . "'],";
                    addToLine(4, $line);

                    echo projectAdded($token, $secret);

                    break 2;
                } while (false);
            }

            //delete a project
            if (isset($_GET["delete-project"]) && isset($_SESSION["access-code"]) && $_SESSION["access-code"] == ACCESS_CODE) {
                if (!isset(PROJECTS[$_GET["delete-project"]])) {
                    echo generateMessage("warning", "Project not found");
                    echo generateMainTop();
                    echo generateMainBody();
                } else {
                    echo deleteProject($_GET["delete-project"]);
                }

                break;
            }

            if (isset($_GET["delete-project-submit"]) && isset($_SESSION["access-code"]) && $_SESSION["access-code"] == ACCESS_CODE) {
                if (!isset(PROJECTS[$_GET["delete-project-submit"]])) {
                    echo generateMessage("warning", "Project not found");
                    echo generateMainTop();
                    echo generateMainBody();
                } else {
                    deleteLine(5 + $_GET["delete-project-submit"]);
                    $_SESSION["message"] = "Project succesfully deleted";
                    $_SESSION["message-type"] = "success";
                    header("Location: esyncer-deploy.php");
                    die();
                }
                break;
            }

            //regenerate token and secret
            if (isset($_GET["regenerate-token"]) && isset($_SESSION["access-code"]) && $_SESSION["access-code"] == ACCESS_CODE) {
                if (!isset(PROJECTS[$_GET["regenerate-token"]])) {
                    echo generateMessage("warning", "Project not found");
                    echo generateMainTop();
                    echo generateMainBody();
                } else {
                    echo regenerateToken($_GET["regenerate-token"]);
                }

                break;
            }

            if (isset($_GET["regenerate-token-submit"]) && isset($_SESSION["access-code"]) && $_SESSION["access-code"] == ACCESS_CODE) {
                if (!isset(PROJECTS[$_GET["regenerate-token-submit"]])) {
                    echo generateMessage("warning", "Project not found");
                    echo generateMainTop();
                    echo generateMainBody();
                } else {
                    $token = "";
                    $tokenOrig = false;
                    do {
                        $token = generateRandom(8);
                        for ($i = 0; $i < count(PROJECTS); $i++) {
                            if (PROJECTS[$i]["token"] == $token) {
                                $tokenOrig = false;
                                break;
                            } else {
                                $tokenOrig = true;
                            }
                        }
                    } while ($tokenOrig == false && count(PROJECTS) != 0);

                    $secret = generateRandom(10);

                    replaceLine(5 + $_GET["regenerate-token-submit"], '        ["name" => "' . PROJECTS[$_GET["regenerate-token-submit"]]["name"] . '", "folder" => "' . PROJECTS[$_GET["regenerate-token-submit"]]["folder"] . '", "token" => "' . $token . '", "secret" => "' . $secret . '"],');

                    $_SESSION["message"] = "Token and secret of project <b>". PROJECTS[$_GET["regenerate-token-submit"]]["name"] ."</b> succesfully regenerated. New token is: <b>" . $token . "</b> and new secret is: <b>" . $secret . "</b>.";
                    $_SESSION["message-type"] = "success";
                    header("Location: esyncer-deploy.php");
                    die();
                }
                break;
            }

            //normal page
            if (!isset($_SESSION["access-code"]) || $_SESSION["access-code"] != ACCESS_CODE) {
                //not logged
                echo generateLoginForm();
                break;
            } else {
                //logged
                //notifications
                if (isset($_SESSION["message"])) {
                    echo generateMessage($_SESSION["message-type"], $_SESSION["message"]);
                    unset($_SESSION["message"]);
                    unset($_SESSION["message-type"]);
                }
                echo generateMainTop();
                echo generateMainBody();
                break;
            }
                
        } while (false);




        //***********************************************************
        //             FUNCTIONS FOR GENERATE HTML
        //***********************************************************

        //FUNCTIONAL FUNCTIONS
        function replaceLine($lineNumber, $newLine) {
            $lineNumber = $lineNumber - 1;
            //rewrite line in file esyncer-vars.php
            $file = fopen("esyncer-vars.php", "r");
            $lines = array();
            $i = 0;
            while(!feof($file)) {
                $lines[] = fgets($file);
                if ($i == $lineNumber) {
                    $lines[$i] = $newLine . "\n";
                }
                $i++;
            }

            fclose($file);

            //write new content
            $file = fopen("esyncer-vars.php", "w");
            foreach ($lines as $line) {
                fwrite($file, $line);
            }
            fclose($file);
        }

        function addToLine($lineNumber, $data) {
            $lineNumber = $lineNumber - 1;
            //rewrite line in file esyncer-vars.php
            $file = fopen("esyncer-vars.php", "r");
            $lines = array();
            $i = 0;
            while(!feof($file)) {
                $lines[] = fgets($file);
                if ($i == $lineNumber) {
                    $lines[$i] = $lines[$i] . $data . "\n";
                }
                $i++;
            }

            fclose($file);

            //write new content
            $file = fopen("esyncer-vars.php", "w");
            foreach ($lines as $line) {
                fwrite($file, $line);
            }
            fclose($file);
        }

        function deleteLine($lineNumber) {
            $lineNumber = $lineNumber - 1;
            //rewrite line in file esyncer-vars.php
            $file = fopen("esyncer-vars.php", "r");
            $lines = array();
            $i = 0;
            while(!feof($file)) {
                $lines[] = fgets($file);
                if ($i == $lineNumber) {
                    $lines[$i] = "";
                }
                $i++;
            }

            fclose($file);

            //write new content
            $file = fopen("esyncer-vars.php", "w");
            foreach ($lines as $line) {
                fwrite($file, $line);
            }
            fclose($file);
        }

        function generateRandom($numberOfChars) {
            $characters = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ';
            $charactersLength = strlen($characters);
            $randomString = '';
            for ($i = 0; $i < $numberOfChars; $i++) {
                $randomString .= $characters[rand(0, $charactersLength - 1)];
            }
            return $randomString;
        }

        //LOGIN AND WELCOME FUNCTIONS
        function generateLoginForm() {
            return '
            <div class="container">
                <div class="row">
                    <div class="col-md-6 offset-md-3 mt-5">
                        <h1>Enplated Syncer</h1>
                        <form action="?login" method="post">
                            <div class="form-group">
                                <label for="access-code">Insert your Access Code:</label>
                                <input type="password" class="form-control" id="access-code" name="access-code" placeholder="Access Code">
                            </div>
                            
                            <button type="submit" class="btn btn-primary mt-3">Login</button>
                        </form>
                    </div>
                </div>
            </div>';
        }

        function generateMessage($type, $message) {
            return '
            <div class="container mt-5">
                <div class="alert alert-'.$type.' alert-dismissible fade show" role="alert">
                    '.$message.'
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </div>
            ';
        }

        function generateWelcome() {
            return '
            <div class="container mt-5">
                <div class="alert alert-dark alert-dismissible fade show" role="alert">
                    <h4 class="alert-heading">Welcome to Enplated Syncer</h4>
                    <p>
                        This is a simple tool to deploy your projects to web server easily with Enplated Syncer.
                        <br>
                        <br>
                        You can learn more about Enplated Syncer <a href="https://github.com/K-cermak/Enplated-Syncer" target="_blank">here</a>.
                        <br>
                        <br>
                        <br>
                        Before you can use this tool, you need to create your access code.
                    </p>
                </div>

                <div class="row">
                    <div class="col-md-6 offset-md-3 mt-5">
                        <form action="?new-access" method="post">
                            <div class="form-group">
                                <label for="access-code">Insert your new Access Code:</label>
                                <input type="password" class="form-control" id="new-access-code" name="new-access-code" placeholder="Access Code">
                                <div class="input-group-append">
                              </div>
                            </div>

                            <div class="form-group mt-3">
                                <label for="access-code">Repeat:</label>
                                <input type="password" class="form-control" id="new-access-code-repeat" name="new-access-code-repeat" placeholder="Repeat Access Code">
                            </div>

                            
                            <button type="submit" class="btn btn-primary mt-3">Save</button>
                        </form>
                    </div>
                </div>
            </div>
            ';
        }

        //LOGGED FUNCTIONS

        function generateLoggedHeader() {
            return '
            <header>
                <nav class="navbar navbar-expand-lg navbar-light bg-light">
                    <div class="container-fluid">
                        <h4><a href="?" class="navbar-brand">Enplated Syncer</a></h4>
                        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                            <span class="navbar-toggler-icon"></span>
                        </button>
                        <div class="collapse navbar-collapse" id="navbarSupportedContent">
                            <ul class="navbar-nav me-auto mb-2 mb-lg-0"></ul>
                                <a href="?change-access" class="btn btn-link">Change Access Code</a>
                                <a href="?logout" class="btn btn-primary">Logout</a>
                        </div>
                    </div>
                </nav>
            </header> 
                        
            ';
        }

        function generateMainTop() {
            $url = $_SERVER['HTTP_HOST'].dirname($_SERVER['PHP_SELF']) . "/" . "esyncer-deploy.php";
            $protocol = ((!empty($_SERVER['HTTPS']) && $_SERVER['HTTPS'] != 'off') || $_SERVER['SERVER_PORT'] == 443) ? "https://" : "http://";

            return '
            <div class="container mt-5">
                <div class="row">
                    <div class="col-md-6 offset-md-3">
                        <div class="card">
                            <div class="card-body text-center">
                                <h5 class="card-title">Welcome back in Enplated Syncer 👋</h5>
                                <p class="card-text">Need help? You can find more on <a href="https://github.com/K-cermak/Enplated-Syncer" target="_blank">GitHub</a>.</p>
                                <a href="?add-folder" class="btn btn-primary">Add a new folder for Deploy</a>
                                <br>
                                <h5 class="mt-5">Your URL for Enplated Syncer:</h5>
                                <code>'. $protocol . $url .'</code>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            ';
        }

        function generateMainBody() {
            $html = '<div class="container mt-5">';

            if (count(PROJECTS) == 0) {
                $html .= '<div class="row">
                    <div class="col-md-6 offset-md-3">
                        <div class="card">
                            <div class="card-body text-center">
                                <h5 class="card-title">No project found</h5>
                                <p class="card-text">You need to add a project first.</p>
                                <a href="?add-folder" class="btn btn-primary">Add a new folder for Deploy</a>
                            </div>
                        </div>
                    </div>
                </div>';
            } else {
                for ($i = 0; $i < count(PROJECTS); $i++) {
                    $html .= '
                        <div class="card">
                            <div class="card-body">
                                <div class="row">
                                    <div class="col">
                                        <h5 class="card-title">'. PROJECTS[$i]['name'] .'</h5>
                                        <p class="card-text mt-2">Folder: <b>'. PROJECTS[$i]['folder'] .'</b></p>
                                    </div>

                                    <div class="col mt-3">
                                        <p class="card-text mb-0 mt-3">Token: <b class="blur">'. PROJECTS[$i]['token'] .'</b></p>
                                        <p class="card-text mb-0">Secret: <b class="blur">'. PROJECTS[$i]['secret'] .'</b></p>
                                    </div>
                                    <div class="col mt-3 text-center">
                                        <a href="?regenerate-token='. $i .'" class="btn btn-primary btn-sm">Regenerate Token and Secret</a>
                                        <br>
                                        <a href="?delete-project='. $i .'" class="btn btn-danger btn-sm mt-3">Delete</a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    ';
                }
            }

            $html .= '</div>';

            return $html;
        }

        //OTHER PAGES
        function changeAccessCode() {
            return '
            <div class="container">
                <div class="row">
                    <div class="col-md-6 offset-md-3 mt-5">
                        <form action="?change-access-code" method="post">
                            <div class="form-group">
                                <label for="access-code">Current Access Code:</label>
                                <input type="password" class="form-control" id="access-code" name="old-access-code" placeholder="Old Access Code">
                            </div>

                            <div class="form-group mt-4">
                                <label for="access-code">New Access Code:</label>
                                <input type="password" class="form-control" id="new-access-code" name="new-access-code" placeholder="New Access Code">
                            </div>

                            <div class="form-group mt-2">
                                <label for="access-code">Repeat:</label>
                                <input type="password" class="form-control" id="new-access-code-repeat" name="new-access-code-repeat" placeholder="Repeat Access Code">
                            </div>
                            
                            <button type="submit" class="btn btn-primary mt-5">Change Access Code</button>
                        </form>
                    </div>
                </div>
            </div>';
        }

        function addProject($projectName = "", $projectFolder = "") {
            $currentFile = __FILE__;
            return '
            <div class="container">
                <div class="row">
                    <div class="col-md-6 offset-md-3 mt-5">
                        <form action="?add-folder-submit" method="post">
                            <div class="form-group">
                                <label for="project-name">Project Name:</label>
                                <input type="text" class="form-control" id="project-name" name="project-name" placeholder="Project Name" value="'. $projectName . '">
                            </div>

                            <div class="form-group mt-4">
                                <label for="project-folder">Project Folder:</label>
                                <p>Current file location: <b>'. $currentFile .'</b></p>
                                <p>Enplated Syncer does not validate if the folder exists or if you have privilage to access and write to it.</p>
                                <input type="text" class="form-control" id="project-folder" name="project-folder" placeholder="Example: /var/www/html/..." value="'. $projectFolder . '">
                            </div>
                            
                            <button type="submit" class="btn btn-primary mt-5">Add Project</button>
                        </form>
                    </div>
                </div>
            </div>';
        }

        function projectAdded($token, $secret) {
            return '
            <div class="container">
                <div class="row">
                    <div class="col-md-6 offset-md-3 mt-5">
                        <div class="card">
                            <div class="card-body text-center">
                                <h5 class="card-title">Project added</h5>
                                <p class="card-text">Your project has been added. You can now deploy your project in Enplated Syncer app.</p>
                                <p class="card-text">Token: <b>'. $token .'</b></p>
                                <p class="card-text">Secret: <b>'. $secret .'</b></p>
                                <a href="?" class="btn btn-link">Go to main page</a>
                                <a href="?add-folder" class="btn btn-primary">Add another project</a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>';
        }

        function deleteProject($projectIndex) {
            return '
            <div class="container">
                <div class="row">
                    <div class="col-md-6 offset-md-3 mt-5">
                        <div class="card">
                            <div class="card-body text-center">
                                <h5 class="card-title">Delete project</h5>
                                Project name : <b>'. PROJECTS[$projectIndex]['name'] .'</b>
                                <br>
                                Project folder : <b>'. PROJECTS[$projectIndex]['folder'] .'</b>

                                <br>
                                <br>

                                <p class="card-text">Are you sure you want to delete this project?</p>
                                <a href="?" class="btn btn-link">No, go back</a>
                                <a href="?delete-project-submit='. $projectIndex .'" class="btn btn-danger ms-5">Yes, delete project</a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>';
        }

        function regenerateToken($projectIndex) {
            return '
            <div class="container">
                <div class="row">
                    <div class="col-md-6 offset-md-3 mt-5">
                        <div class="card">
                            <div class="card-body text-center">
                                <h5 class="card-title">Regenerate token and secret</h5>
                                Project name : <b>'. PROJECTS[$projectIndex]['name'] .'</b>
                                <br>
                                Project folder : <b>'. PROJECTS[$projectIndex]['folder'] .'</b>

                                <br>
                                <br>

                                <p class="card-text">Are you sure you want to regenerate token and secret? Old token and secret will stop work.</p>
                                <a href="?" class="btn btn-link">No, go back</a>
                                <a href="?regenerate-token-submit='. $projectIndex .'" class="btn btn-danger ms-5">Yes, regenerate token and secret</a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>';
        }
    ?>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM" crossorigin="anonymous"></script>
</body>
</html>