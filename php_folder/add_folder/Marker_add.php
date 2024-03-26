<?php
        $con = mysqli_connect("localhost", "jun6726", "duswns11!!", "jun6726");
        mysqli_query ("set names utf8");

        $userid = $_POST['userid'];
        $location = $_POST['location'];
        $date_start = $_POST['date_start'];
        $date_end = $_POST['date_end'];
        $Date_start = date("Y-m-d", strtotime($date_start));
        $Date_end = date("Y-m-d", strtotime($date_end));
        $result = mysqli_query($con, "INSERT INTO Travel (Travel_id, User_id, Date_start, Date_end, Location, shared)
                                                  VALUES ('','$userid', '$Date_start', '$Date_end', '$location', '1');");
//      $result2 = mysqli_query($con, "INSERT into Plan(Plan_id, Travel_id, Day) SELECT shared, User_id, Date_end-Date_start FROM Travel WHERE Travel_id = 79;");
        //datediff
        $Travel_id = mysqli_query($con, "SELECT Travel_id FROM Travel;");
        $DateDiff = mysqli_query($con, "SELECT Travel_id, TIMESTAMPDIFF(day, Date_start, Date_end) FROM Travel ORDER BY Travel_id DESC limit 1;");
        $array = array();
        while($row = mysqli_fetch_array($DateDiff))
        {
                array_push($array, array('Travel_id'=>$row[0], 'DateDiff'=>$row[1]));
                while($row[1] > 0)
                {
                        $insert_plan = mysqli_query($con, "INSERT INTO Plan (Travel_id, Day) VALUES ('$row[0]', '$row[1]');");
                        $row[1]-=1;
                }
                echo $row[0];
        }
        mysqli_close($con);
?>
