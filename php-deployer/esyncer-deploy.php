<?php
    session_start();
    require_once "esyncer-vars.php";
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
</head>
<body>
    <?php
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
                        replaceVariable(2, 'define("ACCESS_CODE", "' . $_POST["new-access-code"] . '");');
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
                    header("Location: " . $_SERVER["REQUEST_URI"]);
                    die();
                } else {
                    echo generateMessage("danger", "Access code is incorrect.");
                }
            }


            //change access code
            if (isset($_GET["change-access"])) {
                echo changeAccessCode();
                break;
            }

            if (isset($_GET["change-access-code"])) {
                //verify that old access code is correct
                if (isset($_POST["old-access-code"]) && $_POST["old-access-code"] == ACCESS_CODE) {
                    //verify that new access code is correct
                    if ($_POST["new-access-code"] == $_POST["new-access-code-repeat"]) {
                        //min 5 chars
                        if (strlen($_POST["new-access-code"]) >= 5) {
                            replaceVariable(2, 'define("ACCESS_CODE", "' . $_POST["new-access-code"] . '");');
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

            if (!isset($_SESSION["access-code"]) || $_SESSION["access-code"] != ACCESS_CODE) {
                //not logged
                echo generateLoginForm();
                break;
            } else {
                //logged
                echo generateMainContent();
                break;
            }
                
        } while (false);

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

        function replaceVariable($lineNumber, $newLine) {
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

        function generateMainContent() {
            $url = $_SERVER['HTTP_HOST'].dirname($_SERVER['PHP_SELF']) . "/" . "esyncer-deploy.php";

            return '
            <div class="container mt-5">
                <div class="row">
                    <div class="col-md-6 offset-md-3">
                        <div class="card">
                            <div class="card-body text-center">
                                <h5 class="card-title">Welcome back in Enplated Syncer 👋</h5>
                                <p class="card-text">Need help? You can find more on <a href="https://github.com/K-cermak/Enplated-Syncer" target="_blank">GitHub</a>.</p>
                                <a href="?sync" class="btn btn-primary">Add a new folder for Deploy</a>
                                <br>
                                <h5 class="mt-5">Your URL for Enplated Syncer:</h5>
                                <code>'. $url .'</code>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            ';
        }
    ?>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM" crossorigin="anonymous"></script>
</body>
</html>