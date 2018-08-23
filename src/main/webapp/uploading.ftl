<!DOCTYPE html>
<html>

<style type="text/css">
    frontPage {
        float: left;
        width: 500%;
    }

    aside {
        float: right;
        width: 500%;
    }

    #wrap {
        width: 700px;
        margin: 0 auto;
    }
</style>
    <head>
        <meta charset="UTF-8">
        <title>Json Data Simulator</title>
    </head>

    <div>
    <frontPage>
        <body onload="updateSize();">
        <form name="uploadingForm" enctype="multipart/form-data" action="/" method="POST">
            <p>
                Configs: <input style="padding-left:3.8em;margin-left:3.8em" id="fileInput" type="file" name="uploadingFiles" onchange="updateSize();" multiple>
                selected files: <span id="fileNum">0</span>;
                total size: <span id="fileSize">0</span>
            </p>
            <p>
                EsUrl:<input style="margin-left:9em" id="esUrl" type="text" value="10.102.4.127" name="esUrl">
            </p>
  	        <p>
                Delete Index (Y/N): <input style="margin-left:2em" id="deleteIndex" type="text" value="N" name="deleteIndex">
            </p>
  			<p>
                User Generator (Y/N): <input style="margin-left:1em" id="useGenerator" type="text" value="N" name="useGenerator">
            </p>
            <p>
                StartIndex: <input style="margin-left:6.5em" id="startIndex" type="text" value="1" name="startIndex">
            </p>
            <p>
                SampleSize:   <input id="sampleSize" style="margin-left:5.7em" type="text" value="50" name="sampleSize">
            </p>
            </br>
             <p>
                <input type="submit" style="margin-left:12em;text-align:center" value="Upload and generate samples">
            </p>
        </form>
       <!-- <div>
            <div>Uploaded json config files and generated samples in zipfile</div>
        	<#list files as file>
            <div>
                <a href="/${file.getName()}" >Download ${file.getName()}</a>
            </div>
        </#list>

        </div> -->
    </frontPage>

    <aside>
        Details about the config.json fields
    </aside>
    </div>
        <script>
            function updateSize() {
                var nBytes = 0,
                        oFiles = document.getElementById("fileInput").files,
                        nFiles = oFiles.length;
                for (var nFileId = 0; nFileId < nFiles; nFileId++) {
                    nBytes += oFiles[nFileId].size;
                }

                var sOutput = nBytes + " bytes";
                // optional code for multiples approximation
                for (var aMultiples = ["KiB", "MiB", "GiB", "TiB", "PiB", "EiB", "ZiB", "YiB"], nMultiple = 0, nApprox = nBytes / 1024; nApprox > 1; nApprox /= 1024, nMultiple++) {
                    sOutput = nApprox.toFixed(3) + " " + aMultiples[nMultiple] + " (" + nBytes + " bytes)";
                }
                // end of optional code

                document.getElementById("fileNum").innerHTML = nFiles;
                document.getElementById("fileSize").innerHTML = sOutput;
            }
        </script>
    </body>
</html>