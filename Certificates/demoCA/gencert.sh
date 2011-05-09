#!/bin/bash

function prompt()
{
	while true; do
		read -p "$1 " username
		if [ -n "$username" ]; then
			break;
		fi;
	done;
	echo $username
}

function usage()
{
	echo "Usage : $0 gen|sign"
	exit 1
}

# Check 3 arguments are given #
if [ $# -ne 1 ]
then
	usage
fi

operation=$1

case "$operation" in
gen) echo "Generating new certificate"
	alias=`prompt "Enter user alias:"`
	keysize=`prompt "Enter public key size:"`
	# Generate a user key and request for signing (csr). 
	openssl genrsa -des3 -out $alias.key $keysize
	openssl req -new -key $alias.key -out $alias.csr
	echo "Done"
	;;
sign) echo "Signing user certificate"
	alias=`prompt "Enter user alias:"`
	validity=`prompt "Enter certificate validity in days:"`
	if [ ! -f ]; then
		echo 0 > serial
		serial=0
	else
		serial=`cat serial`
		let serial++
	fi
	# Sign the certificate signing request (csr) with the self-created Certificate Authority (CA)
	openssl x509 -req -days $validity -in $alias.csr -CA ca.crt -CAkey ca.key -set_serial $serial -out $alias.crt
	echo "Done"
	;;
*) usage
	;;
esac

