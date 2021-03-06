# Checksum Calculator & Validator

This is a tool used to calculate checksums and validate them against their sources. Algorithms are loaded from your local instance of Java, so you are not limited to the ones below.

Help dialog:
```
chk.jar -h
usage: chk
 -d,--diff <arg>     Checks the difference between this hash and the computed one
 -h,--help           Prints this message
 -m,--mode <arg>     Algorithm to use (Defaults to MD5)
 -v,--verbose        Outputs progress of calculation

Available Algorithms:
MD2     sun.security.provider.MD2
MD5     sun.security.provider.MD5
SHA     sun.security.provider.SHA
SHA-224 sun.security.provider.SHA2$SHA224
SHA-256 sun.security.provider.SHA2$SHA256
SHA-384 sun.security.provider.SHA5$SHA384
SHA-512 sun.security.provider.SHA5$SHA512
```

# Use-Cases
Output checksum of file:
```
chk.jar file.txt
702EDCA0B2181C15D457EACAC39DE39B
```
Compare calculated checksum /w source:
```
chk.jar file.txt -d 702EDCA0B2181C15D457EACAC39DE39B
Calculated hash matches!
```
Output checksum of file /w verbosity increased (for larger files):
```
chk.jar file.txt -v
Opening file.txt
100.00% [==================================================]
702EDCA0B2181C15D457EACAC39DE39B
```
