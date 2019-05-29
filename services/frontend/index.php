<?php

include('httpful.phar');

$response = \Httpful\Request::get('cat-service-backend:8989/cats')->send();

echo '<form method="post" action="">';
echo '<table><th>ID</th><th>Name</th><th>Image</th><tr/>';
$catArray = json_decode($response);
foreach($catArray as $cat) {
	
	echo '<td>' . $cat->id . '</td>';
	echo '<td>' . $cat->name . '</td>';
	echo '<td><img src=' . $cat->imageUrl . '></td>';
	echo '<td><button class="sdelete" name="sdelete" value="'. $cat->id . '">Delete</button></td><tr/>';
}
echo '</table></form>';

if(isset($_POST['sdelete'])) {
    $id = $_POST['sdelete'];
    //use $id to delete a single entry from DB, and then produce new output
	$delete_response = \Httpful\Request::delete('cat-service-backend:8989/cats/' . $id)->send();
	echo "<meta http-equiv='refresh' content='0'>";
}

?>