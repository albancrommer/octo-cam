#!/usr/bin/php
<?php
// http://stackoverflow.com/questions/10071768/video-streaming-from-android-device-to-lamp-server

$socket = stream_socket_server("tcp://0.0.0.0:12345", $errno, $errstr);
$file = "/tmp/video.3gp";
$threegp_header = "\x00\x00\x00\x18\x66\x74\x79\x70\x33\x67\x70\x34\x00\x00\x03\x00\x33\x67\x70\x34\x33\x67\x70\x36";
$four_bytes = "\x00\x00\x00\x00";

if (!$socket) {

  echo "$errstr ($errno)\n";

} else {

  echo "server start listening\n";

  while ( $conn = stream_socket_accept($socket, 180))
  {
    echo "phone connected\n";
    if( isset($init)){
        break;
    }
    $init = true;
        
    $handle = fopen($file,"w");

    //mediaRecorder gives invalid stream header, so I replace it discarding first 32 byte, replacing with 28 good byte (standard 3gp header plus 4 empty bytes)
    $discard = stream_get_contents($conn, 32);
    fwrite($handle, $threegp_header);
    fwrite($handle, $four_bytes);

    //then confinue to write stream on file until phone stop streaming
        while(!feof($conn))
        {
	echo ">";
        fwrite($handle, stream_get_contents($conn, 1500));
        }
    echo "phone disconnected\n";
    fclose($handle);

    //then i had to update 3gp header (bytes 25 to 28) with the offset where moov atom starts
    $handle = fopen($file,"c"); 
    $output = shell_exec('grep -aobE "moov" '.$file);
    $moov_pos = preg_replace('/moov:(\d+)/i', '\\1', $output);
    $moov_pos_ex = strtoupper(str_pad(dechex($moov_pos - 24), 8, "0", STR_PAD_LEFT));
    fwrite($handle, $threegp_header);
    var_dump($moov_pos_ex,str_split($moov_pos_ex,2));
    $tmp = '';
        foreach(str_split($moov_pos_ex,2) as $hex)
        {
		echo "Splitting\n";
                 $tmp .= pack('C*', hexdec($hex));
        }
    echo "Writing\n";
    fwrite($handle, $tmp);
    echo "Closing\n";
    fclose($handle);


  }
  echo "Done\n";


}
  @fclose($handle);
  fclose($socket);
