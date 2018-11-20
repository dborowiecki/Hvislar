<?php
    #$con = mysqli_connect("mysql10.000webhost.com", "a3288368_user", "abcd1234", "a3288368_data");
    $con = pg_connect("host=localhost port=5432 dbname=hvislar_db user=postgres password=admin");
    $username = $_POST["username"];
    #$statement = mysqli_prepare($con, "SELECT * FROM user WHERE username = ? AND password = ?");
    $fetcher = pg_query($con, "SELECT username from account where id_account_pk in (SELECT id_account_fk from contact where (id_user_fk = (select id_account_pk from account where username = '".$username."')))");  
    $response["users"] = array();
    $response["success"] = false;
    while($row = pg_fetch_array($fetcher)){
        $response["success"] = true; 
        array_push($response["users"], $row["username"]);
    }
    echo json_encode($response);
?>