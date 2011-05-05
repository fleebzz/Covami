<?php
//header('Content-type: text/plain');
class Db{
	private static $instance;

	private $cnx = null;
	
	private function __construct(){
		$this->cnx = mysql_connect('127.0.0.1', 'root', 'root');
		if(!$this->cnx){
			echo 'Database error';
			echo "<br/>".mysql_error();
			exit();
		}
		
		mysql_select_db('COVAMI', $this->cnx);
		mysql_query("SET NAMES 'utf8'",$this->cnx);
		
	}
	
	public function __destruct(){
		@mysql_close($this->cnx);
	}
	
	public static function getInstance(){
		if (!isset(self::$instance)){
			$c = __CLASS__;
			self::$instance = new $c();
			//self::$instance->__construct();
		}

		return self::$instance;
	}
	
	/* -- Helpers -- */
	public function _ass($mysqlRessource){
		if(mysql_error())
			echo '<h1>'.mysql_error().'</h1></br>';
			
		if(!$mysqlRessource || mysql_num_rows($mysqlRessource) != 1)
			return array();
			
		return mysql_fetch_array($mysqlRessource,MYSQL_ASSOC);
	}
	
	public function _num($mysqlRessource){
		if(mysql_error())
			echo '<h1>'.mysql_error().'</h1></br>';
			
		if(!$mysqlRessource || mysql_num_rows($mysqlRessource) != 1)
			return array();
			
		return mysql_fetch_array($mysqlRessource,MYSQL_NUM);
	}
	
	public function getIdFromInsee($insee){
	  $a =  $this->_num(mysql_query('select id from City where insee = "'.$insee.'"'));
	  return $a[0];
	}
	
	public function makeRoad($City_id, $neighborhood_id){
	  
	  $query = "INSERT INTO  `COVAMI`.`City_City` (
    `City_id` ,
    `neighborhood_id`
    )
    VALUES (
    '".$City_id."',  '".$neighborhood_id."'
    );";
    
	  $res = mysql_query($query);
    
    echo $query."</br>";
    
    if(mysql_error())
			echo '<h1>'.mysql_error().'</h1></br>';
	}

};

$db = Db::getInstance();

$f = file_get_contents("roads.csv");

$lines = explode("\n",$f);
array_pop($lines);

for ($i=0, $iM = count($lines); $i < $iM; $i++) { 
  
  $insees = explode(";", trim($lines[$i]));
  
  $City_id = $db->getIdFromInsee($insees[0]);
  
  for ($x= 1, $xM = count($insees); $x < $xM; $x++) { 
    
    if(!empty($insees[$x])){
      $neighborhood_id = $db->getIdFromInsee($insees[$x]);
      
      //echo $insees[0].' - '. $insees[$x] ."</br>";
      //echo $City_id. ' - '. $neighborhood_id."</br>";
      $db->makeRoad($City_id, $neighborhood_id);
    }
    
    
    //
  }
  
}

?>