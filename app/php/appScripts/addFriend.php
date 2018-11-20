<?php
    $con = pg_connect("host=localhost port=5432 dbname=hvislar_db user=postgres password=admin");
    $username = $_POST["username"];
    $friend_name = $_POST["friend_name"];

    $fetcher = pg_query($con, "SELECT id_account_pk FROM account WHERE username = '".$friend_name."'");
    $row = pg_fetch_array($fetcher);
    $friend_id = $row["id_account_pk"];
    $fetcher = pg_query($con, "SELECT id_account_pk FROM account WHERE username = '".$username."'");
    $row = pg_fetch_array($fetcher);
    $user_id = $row["id_account_pk"];

    $statement = pg_prepare($con, "add_contact", "INSERT INTO contact (id_user_fk, id_account_fk) VALUES ($1, $2)");
    $res = pg_execute($con, "add_contact", array((int)$user_id, (int)$friend_id));
    $response = array();
    $response["success"] = $res;
    
    echo json_encode($response);
?>