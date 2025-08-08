select ip in $(cat targets.list) exit; do 
   case $ip in
      exit) echo "exiting"
            break ;;
         *) echo ip $ip;
   esac
done
