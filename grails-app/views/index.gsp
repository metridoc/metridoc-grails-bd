<html>
    <head>
        <title>Welcome to Datafarm</title>
        <style type="text/css" media="screen">

        #pageBody {
            margin-left:20px;
            margin-right:20px;
        }
        </style>
    </head>
    <body>
        <div id="pageBody">
           <div id="controllerList" class="dialog">
                <h2>Datafarm Available Reports:</h2>
                <ul>
                    <g:each var="c" in="${grailsApplication.controllerClasses.sort { it.fullName } }">
                        <li class="controller"><g:link controller="${c.logicalPropertyName}">${c.logicalPropertyName}</g:link></li>
                    </g:each>
                </ul>
            </div>
        </div>
    </body>
</html>
